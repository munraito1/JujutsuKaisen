package controllers;

import enums.BattleState;
import models.Combatant;
import utils.Position;

/**
 * Observer interface for battle events.
 */
public interface BattleListener {
    void onBattleStarted();
    void onTurnStarted(Combatant unit);
    void onUnitMoved(Combatant unit, Position from, Position to);
    void onUnitAttacked(Combatant attacker, Combatant target, int damage);
    void onTechniqueUsed(Combatant user, Combatant target, String techniqueName,
                         int damage, String animationType, Position targetPos);
    void onUnitDefended(Combatant unit);
    void onUnitDefeated(Combatant unit);
    void onBattleEnded(BattleState result);
    void onMessage(String message);
}
