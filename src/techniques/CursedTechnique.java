package techniques;

import models.Combatant;

public interface CursedTechnique {

    String getName();

    String getDescription();

    int getCursedEnergyCost();

    int getCooldown();

    int getRange();

    default String getAnimationType() { return "HIT"; }

    default boolean canTriggerBlackFlash() { return false; }

    default int getBlackFlashChance() { return 0; }

    void execute(Combatant user, Combatant target);
}
