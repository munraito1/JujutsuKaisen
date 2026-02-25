package techniques;

import models.Combatant;

public class SpearThrust implements CursedTechnique {

    @Override public String getName()            { return "Spear Thrust"; }
    @Override public String getDescription()     { return "A piercing ranged strike with a special grade spear."; }
    @Override public int getCursedEnergyCost()   { return 0; }
    @Override public int getCooldown()           { return 1; }
    @Override public int getRange()              { return 3; }
    @Override public String getAnimationType()   { return "HIT"; }
    @Override public boolean canTriggerBlackFlash() { return true; }
    @Override public int getBlackFlashChance()       { return 12; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.0);
        target.takeDamage(damage);
    }
}
