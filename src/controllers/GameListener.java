package controllers;

import enums.BattleState;
import models.District;

/**
 * Observer interface for global game map events.
 */
public interface GameListener {
    void onDistrictChanged(District district);
    void onTurnAdvanced(int turnNumber);
    void onBattleStarted(District district);
    void onBattleFinished(District district, BattleState result);
    void onMessage(String message);
}
