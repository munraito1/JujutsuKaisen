package controllers;

import enums.BattleState;
import enums.DistrictStatus;
import enums.GamePhase;
import models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game manager for the global map layer.
 * Manages the world map, player team, current position, and turn cycle.
 */
public class GameManager {

    private final WorldMap worldMap;
    private final SorcererTeam playerTeam;
    private District currentDistrict;
    private int turnNumber;
    private GamePhase phase;
    private final List<GameListener> listeners;

    public GameManager(SorcererTeam playerTeam) {
        this.playerTeam = playerTeam;
        this.worldMap = new WorldMap();
        this.listeners = new ArrayList<>();
        this.turnNumber = 1;
        this.phase = GamePhase.MAP_OVERVIEW;

        worldMap.buildDefaultMap();
        currentDistrict = worldMap.getStartingDistrict();
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Move the player to a neighboring district.
     * Returns true if movement was successful.
     */
    public boolean moveToDistrict(District target) {
        if (target == null) return false;
        if (target.getStatus() == DistrictStatus.LOCKED) {
            fireMessage("Cannot travel to " + target.getName() + " - district is locked!");
            return false;
        }
        if (!currentDistrict.isNeighbor(target)) {
            fireMessage("Cannot travel to " + target.getName() + " - not adjacent!");
            return false;
        }

        currentDistrict = target;
        phase = GamePhase.MAP_OVERVIEW;

        fireDistrictChanged(currentDistrict);
        fireMessage("Arrived at " + target.getName() + ".");
        return true;
    }

    /**
     * Start a mission (battle) in the current district.
     * Returns the enemy team if enemies exist, null otherwise.
     */
    public SorcererTeam startMission() {
        if (!currentDistrict.hasEnemies()) {
            fireMessage("No enemies in " + currentDistrict.getName() + ".");
            return null;
        }

        phase = GamePhase.IN_BATTLE;
        SorcererTeam enemies = currentDistrict.getCombinedEnemyTeam();
        fireBattleStarted(currentDistrict);
        return enemies;
    }

    /**
     * Called when a battle finishes. Updates district status based on result.
     */
    public void onBattleFinished(BattleState result) {
        phase = GamePhase.MAP_OVERVIEW;

        if (result == BattleState.VICTORY) {
            currentDistrict.clearEnemies();
            currentDistrict.setStatus(DistrictStatus.CONTROLLED);
            fireMessage(currentDistrict.getName() + " is now under control!");

            // Check if any locked districts should unlock
            worldMap.updateLockedDistricts();
        } else {
            fireMessage("Mission failed! Retreating to base...");
            // Retreat to starting district
            currentDistrict = worldMap.getStartingDistrict();
        }

        fireBattleFinished(currentDistrict, result);
        fireDistrictChanged(currentDistrict);
    }

    /**
     * End the current turn. Heals team at base, may spawn new enemies, advances turn counter.
     */
    public void endTurn() {
        turnNumber++;

        // Heal team if at Jujutsu High (base)
        District base = worldMap.getStartingDistrict();
        if (currentDistrict == base) {
            for (Combatant c : playerTeam.getAliveMembers()) {
                int healAmount = (int) (c.getMaxHp() * 0.3); // 30% heal at base
                c.heal(healAmount);
            }
            fireMessage("Team rests at Jujutsu High. HP restored.");
        }

        // Random curse spawn in controlled districts (10% chance each)
        for (District d : worldMap.getDistricts()) {
            if (d.getStatus() == DistrictStatus.CONTROLLED && d != base) {
                if (Math.random() < 0.10) {
                    d.setStatus(DistrictStatus.CONTESTED);
                    SorcererTeam newEnemies = new SorcererTeam("New Curses");
                    newEnemies.addMember(CursedSpiritMob.createSwarm());
                    d.addEnemyTeam(newEnemies);
                    d.setCurseLevel(1);
                    fireMessage("Curse activity detected in " + d.getName() + "!");
                }
            }
        }

        fireTurnAdvanced(turnNumber);
    }

    // --- Getters ---

    public WorldMap getWorldMap() { return worldMap; }
    public SorcererTeam getPlayerTeam() { return playerTeam; }
    public District getCurrentDistrict() { return currentDistrict; }
    public int getTurnNumber() { return turnNumber; }
    public GamePhase getPhase() { return phase; }

    // --- Event firing ---

    private void fireDistrictChanged(District d) {
        for (GameListener l : listeners) l.onDistrictChanged(d);
    }

    private void fireTurnAdvanced(int turn) {
        for (GameListener l : listeners) l.onTurnAdvanced(turn);
    }

    private void fireBattleStarted(District d) {
        for (GameListener l : listeners) l.onBattleStarted(d);
    }

    private void fireBattleFinished(District d, BattleState result) {
        for (GameListener l : listeners) l.onBattleFinished(d, result);
    }

    private void fireMessage(String msg) {
        for (GameListener l : listeners) l.onMessage(msg);
    }
}
