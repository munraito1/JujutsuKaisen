package controllers;

import models.Combatant;
import techniques.CursedTechnique;
import utils.Position;

import java.util.Comparator;
import java.util.List;

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

        boolean acted = false;
        List<CursedTechnique> available = battleManager.getAvailableTechniques();
        if (!available.isEmpty()) {
            // Sort by effective power: cost * (2 if blackFlash-capable, else 1)
            available.sort((a, b) -> {
                int pa = a.getCursedEnergyCost() * (a.canTriggerBlackFlash() ? 2 : 1);
                int pb = b.getCursedEnergyCost() * (b.canTriggerBlackFlash() ? 2 : 1);
                return pb - pa;
            });
            for (CursedTechnique tech : available) {
                List<Combatant> techTargets = battleManager.getTechniqueTargets(tech);
                if (!techTargets.isEmpty()) {
                    Combatant best = pickBestTarget(techTargets, battleManager.getCurrentUnit());
                    battleManager.useTechnique(tech, best);
                    acted = true;
                    break;
                }
            }
        }

        if (!acted) {
            List<Combatant> attackTargets = battleManager.getAttackableTargets();
            if (!attackTargets.isEmpty()) {
                Combatant bestTarget = pickBestTarget(attackTargets,
                        battleManager.getCurrentUnit());
                battleManager.basicAttack(bestTarget);
                acted = true;
            }
        }

        if (!acted) {
            battleManager.defend();
        }

        battleManager.endTurn();
    }

    /** Prefer killable targets (one-shot), then most dangerous (highest ATK). */
    private Combatant pickBestTarget(List<Combatant> targets, Combatant attacker) {
        if (attacker != null) {
            int estimatedDmg = attacker.getAttack();
            for (Combatant t : targets) {
                if (estimatedDmg >= t.getHp()) return t;
            }
        }
        return targets.stream()
                .max(Comparator.comparingInt(Combatant::getAttack))
                .orElse(targets.get(0));
    }
}
