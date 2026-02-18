package techniques;

import models.Combatant;

/**
 * Black Flash — a spatial distortion that occurs when cursed energy
 * is applied within 0.000001 seconds of a physical hit.
 * Deals 2.5x damage. 3-turn cooldown. Melee range.
 */
public class BlackFlash implements CursedTechnique {

    @Override public String getName() { return "Black Flash"; }
    @Override public String getDescription() { return "Spatial distortion — 2.5x critical impact."; }
    @Override public int getCursedEnergyCost() { return 25; }
    @Override public int getCooldown() { return 3; }
    @Override public int getRange() { return 1; }

    @Override
    public String getAnimationType() { return "BLACK_FLASH"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.5);
        target.takeDamage(damage);
    }
}
