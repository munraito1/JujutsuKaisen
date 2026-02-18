package controllers;

import enums.BattleState;
import enums.TileType;
import models.*;
import techniques.CursedTechnique;
import utils.Position;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core battle engine. Manages grid, turn order, movement, combat,
 * technique cooldowns, and win/lose conditions.
 */
public class BattleManager {

    public static final int GRID_SIZE = 10;

    private final Tile[][] grid;
    private final Map<Combatant, Position> unitPositions;
    private final Map<Position, Combatant> positionToUnit;
    private final List<Combatant> turnOrder;
    private int currentTurnIndex;
    private int roundNumber;
    private BattleState state;
    private SorcererTeam playerTeam;
    private SorcererTeam enemyTeam;
    private final Set<Combatant> defendingUnits;
    private boolean currentUnitMoved;
    private boolean currentUnitActed;
    private final List<BattleListener> listeners;

    // Cooldown tracking: technique instance -> remaining turns
    private final Map<CursedTechnique, Integer> cooldowns;

    private static final Position[] PLAYER_STARTS = {
            new Position(1, 2), new Position(1, 4), new Position(1, 6),
            new Position(0, 3), new Position(0, 5)
    };
    private static final Position[] ENEMY_STARTS = {
            new Position(8, 2), new Position(8, 4), new Position(8, 6),
            new Position(9, 3), new Position(9, 5)
    };

    public BattleManager() {
        grid = new Tile[GRID_SIZE][GRID_SIZE];
        unitPositions = new HashMap<>();
        positionToUnit = new HashMap<>();
        turnOrder = new ArrayList<>();
        defendingUnits = new HashSet<>();
        listeners = new ArrayList<>();
        cooldowns = new HashMap<>();
        state = BattleState.PREPARING;
        roundNumber = 0;
        initGrid();
    }

    private void initGrid() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                grid[x][y] = new Tile(TileType.PLAINS);
            }
        }
    }

    // ==================== Setup ====================

    public void initBattle(SorcererTeam player, SorcererTeam enemy) {
        this.playerTeam = player;
        this.enemyTeam = enemy;

        List<Combatant> playerUnits = player.getAliveMembers();
        for (int i = 0; i < playerUnits.size() && i < PLAYER_STARTS.length; i++) {
            placeUnit(playerUnits.get(i), PLAYER_STARTS[i]);
        }

        List<Combatant> enemyUnits = enemy.getAliveMembers();
        for (int i = 0; i < enemyUnits.size() && i < ENEMY_STARTS.length; i++) {
            placeUnit(enemyUnits.get(i), ENEMY_STARTS[i]);
        }

        calculateTurnOrder();
    }

    private void placeUnit(Combatant unit, Position pos) {
        unitPositions.put(unit, pos);
        positionToUnit.put(pos, unit);
    }

    private void calculateTurnOrder() {
        turnOrder.clear();
        List<Combatant> allAlive = new ArrayList<>();
        allAlive.addAll(playerTeam.getAliveMembers());
        allAlive.addAll(enemyTeam.getAliveMembers());
        allAlive.removeIf(c -> !unitPositions.containsKey(c));
        allAlive.sort((a, b) -> b.getSpeed() - a.getSpeed());
        turnOrder.addAll(allAlive);
    }

    public void startBattle() {
        roundNumber = 1;
        currentTurnIndex = 0;
        for (BattleListener l : listeners) l.onBattleStarted();
        fireMessage("=== Battle started! Round 1 ===");
        startCurrentTurn();
    }

    // ==================== Turn management ====================

    private void startCurrentTurn() {
        while (currentTurnIndex < turnOrder.size() && !turnOrder.get(currentTurnIndex).isAlive()) {
            currentTurnIndex++;
        }

        if (currentTurnIndex >= turnOrder.size()) {
            roundNumber++;
            calculateTurnOrder();
            currentTurnIndex = 0;
            defendingUnits.clear();
            tickCooldowns();

            if (turnOrder.isEmpty()) {
                checkBattleEnd();
                return;
            }

            fireMessage("=== Round " + roundNumber + " ===");
            startCurrentTurn();
            return;
        }

        Combatant current = turnOrder.get(currentTurnIndex);
        currentUnitMoved = false;
        currentUnitActed = false;

        state = isPlayerUnit(current) ? BattleState.PLAYER_TURN : BattleState.ENEMY_TURN;

        for (BattleListener l : listeners) l.onTurnStarted(current);
    }

    public void endTurn() {
        currentTurnIndex++;

        BattleState endState = checkBattleEnd();
        if (endState == BattleState.VICTORY || endState == BattleState.DEFEAT) {
            state = endState;
            for (BattleListener l : listeners) l.onBattleEnded(state);
            return;
        }

        startCurrentTurn();
    }

    // ==================== Movement ====================

    public List<Position> getMovablePositions() {
        if (currentUnitMoved) return Collections.emptyList();
        Combatant unit = getCurrentUnit();
        if (unit == null) return Collections.emptyList();

        Position start = unitPositions.get(unit);
        int range = unit.getMovementRange();

        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        Map<Position, Integer> distances = new HashMap<>();

        queue.add(start);
        distances.put(start, 0);
        visited.add(start);

        List<Position> movable = new ArrayList<>();

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int dist = distances.get(current);

            if (dist > 0 && !positionToUnit.containsKey(current)) {
                movable.add(current);
            }

            if (dist < range) {
                for (Position neighbor : current.getNeighbors()) {
                    if (isValidPosition(neighbor) && !visited.contains(neighbor)
                            && grid[neighbor.getX()][neighbor.getY()].isWalkable()
                            && !positionToUnit.containsKey(neighbor)) {
                        visited.add(neighbor);
                        distances.put(neighbor, dist + 1);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return movable;
    }

    public boolean moveUnit(Position target) {
        if (currentUnitMoved) return false;
        Combatant unit = getCurrentUnit();
        if (unit == null) return false;

        List<Position> movable = getMovablePositions();
        if (!movable.contains(target)) return false;

        Position from = unitPositions.get(unit);
        positionToUnit.remove(from);
        unitPositions.put(unit, target);
        positionToUnit.put(target, unit);
        currentUnitMoved = true;

        for (BattleListener l : listeners) l.onUnitMoved(unit, from, target);
        return true;
    }

    // ==================== Basic Attack ====================

    public List<Combatant> getAttackableTargets() {
        if (currentUnitActed) return Collections.emptyList();
        Combatant unit = getCurrentUnit();
        if (unit == null) return Collections.emptyList();

        Position pos = unitPositions.get(unit);
        int range = unit.getAttackRange();
        List<Combatant> enemies = isPlayerUnit(unit)
                ? enemyTeam.getAliveMembers() : playerTeam.getAliveMembers();

        List<Combatant> targets = new ArrayList<>();
        for (Combatant enemy : enemies) {
            Position enemyPos = unitPositions.get(enemy);
            if (enemyPos != null && pos.chebyshevDistance(enemyPos) <= range) {
                targets.add(enemy);
            }
        }
        return targets;
    }

    public int basicAttack(Combatant target) {
        if (currentUnitActed) return 0;
        Combatant attacker = getCurrentUnit();
        if (attacker == null) return 0;

        int damage = attacker.getAttack();
        if (defendingUnits.contains(target)) {
            damage = (int) (damage * 0.5);
        }

        int hpBefore = target.getHp();
        target.takeDamage(damage);
        int actualDamage = hpBefore - target.getHp();
        currentUnitActed = true;

        for (BattleListener l : listeners) l.onUnitAttacked(attacker, target, actualDamage);

        if (!target.isAlive()) {
            handleUnitDeath(target);
        }
        return actualDamage;
    }

    // ==================== Technique System ====================

    /** Get all techniques for the current unit. */
    public List<CursedTechnique> getAllTechniquesForUnit(Combatant unit) {
        if (unit instanceof NamedSorcerer) return ((NamedSorcerer) unit).getTechniques();
        if (unit instanceof SpecialCurse) return ((SpecialCurse) unit).getTechniques();
        return Collections.emptyList();
    }

    /** Get techniques that are off cooldown and affordable for the current unit. */
    public List<CursedTechnique> getAvailableTechniques() {
        if (currentUnitActed) return Collections.emptyList();
        Combatant unit = getCurrentUnit();
        if (unit == null) return Collections.emptyList();

        int ce = getUnitCE(unit);
        List<CursedTechnique> all = getAllTechniquesForUnit(unit);

        return all.stream()
                .filter(t -> getCooldownRemaining(t) == 0 && t.getCursedEnergyCost() <= ce)
                .collect(Collectors.toList());
    }

    /** Get enemies in range of a specific technique. */
    public List<Combatant> getTechniqueTargets(CursedTechnique tech) {
        if (currentUnitActed) return Collections.emptyList();
        Combatant unit = getCurrentUnit();
        if (unit == null) return Collections.emptyList();

        Position pos = unitPositions.get(unit);
        List<Combatant> enemies = isPlayerUnit(unit)
                ? enemyTeam.getAliveMembers() : playerTeam.getAliveMembers();

        return enemies.stream()
                .filter(e -> {
                    Position ePos = unitPositions.get(e);
                    return ePos != null && pos.chebyshevDistance(ePos) <= tech.getRange();
                })
                .collect(Collectors.toList());
    }

    /** Execute a chosen technique on a target. Handles CE, cooldown, events. */
    public boolean useTechnique(CursedTechnique tech, Combatant target) {
        if (currentUnitActed) return false;
        Combatant user = getCurrentUnit();
        if (user == null) return false;

        int cost = tech.getCursedEnergyCost();
        if (!spendCE(user, cost)) return false;

        // Capture position before damage (target may die)
        Position targetPos = unitPositions.get(target);

        int hpBefore = target.getHp();
        if (defendingUnits.contains(target)) {
            // Defending reduces technique damage too
            int tempDef = target.getDefense();
            // We let execute() handle raw damage; defense subtraction is in takeDamage
        }
        tech.execute(user, target);
        int actualDamage = hpBefore - target.getHp();

        cooldowns.put(tech, tech.getCooldown());
        currentUnitActed = true;

        for (BattleListener l : listeners) {
            l.onTechniqueUsed(user, target, tech.getName(), actualDamage,
                    tech.getAnimationType(), targetPos);
        }

        if (!target.isAlive()) {
            handleUnitDeath(target);
        }
        return true;
    }

    /** Check if the unit has any usable technique (for UI enable/disable). */
    public boolean hasUsableTechniques(Combatant unit) {
        if (currentUnitActed) return false;
        int ce = getUnitCE(unit);
        List<CursedTechnique> all = getAllTechniquesForUnit(unit);
        return all.stream().anyMatch(t -> getCooldownRemaining(t) == 0 && t.getCursedEnergyCost() <= ce);
    }

    public int getCooldownRemaining(CursedTechnique tech) {
        return cooldowns.getOrDefault(tech, 0);
    }

    private void tickCooldowns() {
        cooldowns.replaceAll((tech, cd) -> Math.max(0, cd - 1));
    }

    // ==================== Defend ====================

    public void defend() {
        if (currentUnitActed) return;
        Combatant unit = getCurrentUnit();
        if (unit == null) return;

        defendingUnits.add(unit);
        currentUnitActed = true;
        currentUnitMoved = true;

        for (BattleListener l : listeners) l.onUnitDefended(unit);
    }

    // ==================== Death ====================

    private void handleUnitDeath(Combatant unit) {
        Position pos = unitPositions.get(unit);
        if (pos != null) {
            positionToUnit.remove(pos);
        }
        unitPositions.remove(unit);

        for (BattleListener l : listeners) l.onUnitDefeated(unit);
    }

    private BattleState checkBattleEnd() {
        if (playerTeam.isDefeated()) return BattleState.DEFEAT;
        if (enemyTeam.isDefeated()) return BattleState.VICTORY;
        return state;
    }

    // ==================== Queries ====================

    public Combatant getCurrentUnit() {
        if (currentTurnIndex >= 0 && currentTurnIndex < turnOrder.size()) {
            return turnOrder.get(currentTurnIndex);
        }
        return null;
    }

    public boolean isPlayerUnit(Combatant c) {
        return playerTeam.getMembers().contains(c);
    }

    public Position getUnitPosition(Combatant c) { return unitPositions.get(c); }
    public Combatant getUnitAt(Position p) { return positionToUnit.get(p); }
    public Tile getTile(int x, int y) { return grid[x][y]; }

    public boolean isCurrentUnitMoved() { return currentUnitMoved; }
    public boolean isCurrentUnitActed() { return currentUnitActed; }
    public BattleState getState() { return state; }
    public int getRoundNumber() { return roundNumber; }
    public List<Combatant> getTurnOrder() { return Collections.unmodifiableList(turnOrder); }
    public SorcererTeam getPlayerTeam() { return playerTeam; }
    public SorcererTeam getEnemyTeam() { return enemyTeam; }
    public Map<Combatant, Position> getUnitPositions() { return Collections.unmodifiableMap(unitPositions); }

    public void addListener(BattleListener listener) { listeners.add(listener); }

    // ==================== Helpers ====================

    private boolean isValidPosition(Position p) {
        return p.getX() >= 0 && p.getX() < GRID_SIZE
                && p.getY() >= 0 && p.getY() < GRID_SIZE;
    }

    private int getUnitCE(Combatant unit) {
        if (unit instanceof Sorcerer) return ((Sorcerer) unit).getCursedEnergy();
        if (unit instanceof CursedSpirit) return ((CursedSpirit) unit).getCursedEnergy();
        return 0;
    }

    private boolean spendCE(Combatant unit, int amount) {
        if (unit instanceof Sorcerer) return ((Sorcerer) unit).useCursedEnergy(amount);
        if (unit instanceof CursedSpirit) return ((CursedSpirit) unit).useCursedEnergy(amount);
        return false;
    }

    private void fireMessage(String message) {
        for (BattleListener l : listeners) l.onMessage(message);
    }
}
