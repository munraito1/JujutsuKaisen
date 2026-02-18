package controllers;

import models.Combatant;
import techniques.CursedTechnique;
import utils.Position;

import java.util.Comparator;
import java.util.List;

/**
 * Simple AI for enemy turns.
 * Strategy: move toward closest player, use best available technique, or basic attack.
 */
public class AIController {

    private final BattleManager battleManager;

    public AIController(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    public void executeTurn() {
        Combatant unit = battleManager.getCurrentUnit();
        if (unit == null || !unit.isAlive()) {
            battleManager.endTurn();
            return;
        }

        Position myPos = battleManager.getUnitPosition(unit);
        List<Combatant> targets = battleManager.getPlayerTeam().getAliveMembers();

        if (targets.isEmpty() || myPos == null) {
            battleManager.endTurn();
            return;
        }

        // Find closest player unit
        Combatant closestTarget = null;
        int minDist = Integer.MAX_VALUE;
        for (Combatant target : targets) {
            Position targetPos = battleManager.getUnitPosition(target);
            if (targetPos != null) {
                int dist = myPos.chebyshevDistance(targetPos);
                if (dist < minDist) {
                    minDist = dist;
                    closestTarget = target;
                }
            }
        }

        if (closestTarget == null) {
            battleManager.endTurn();
            return;
        }

        // Move toward closest target
        Position targetPos = battleManager.getUnitPosition(closestTarget);
        List<Position> movable = battleManager.getMovablePositions();
        if (!movable.isEmpty() && targetPos != null) {
            Position bestMove = null;
            int bestDist = myPos.chebyshevDistance(targetPos);
            for (Position p : movable) {
                int dist = p.chebyshevDistance(targetPos);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestMove = p;
                }
            }
            if (bestMove != null) {
                battleManager.moveUnit(bestMove);
            }
        }

        // Try techniques (sorted by cost desc = strongest first)
        boolean acted = false;
        List<CursedTechnique> available = battleManager.getAvailableTechniques();
        if (!available.isEmpty()) {
            available.sort((a, b) -> b.getCursedEnergyCost() - a.getCursedEnergyCost());
            for (CursedTechnique tech : available) {
                List<Combatant> techTargets = battleManager.getTechniqueTargets(tech);
                if (!techTargets.isEmpty()) {
                    Combatant best = pickWeakest(techTargets);
                    battleManager.useTechnique(tech, best);
                    acted = true;
                    break;
                }
            }
        }

        // Fallback: basic attack
        if (!acted) {
            List<Combatant> attackTargets = battleManager.getAttackableTargets();
            if (!attackTargets.isEmpty()) {
                battleManager.basicAttack(pickWeakest(attackTargets));
                acted = true;
            }
        }

        // If nothing possible, defend
        if (!acted) {
            battleManager.defend();
        }

        battleManager.endTurn();
    }

    private Combatant pickWeakest(List<Combatant> targets) {
        return targets.stream()
                .min(Comparator.comparingInt(Combatant::getHp))
                .orElse(targets.get(0));
    }
}
