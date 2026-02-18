package techniques;

import models.Combatant;

/**
 * Interface representing a cursed technique that can be used in combat.
 */
public interface CursedTechnique {

    String getName();

    String getDescription();

    int getCursedEnergyCost();

    /** Cooldown in turns after use (0 = usable every turn). */
    int getCooldown();

    /** Max range in tiles (Chebyshev distance). */
    int getRange();

    /** Animation type: "NONE", "HIT", "BLACK_FLASH". */
    default String getAnimationType() { return "HIT"; }

    /** Apply the technique's effect (damage, heal, etc.). CE is handled by BattleManager. */
    void execute(Combatant user, Combatant target);
}
