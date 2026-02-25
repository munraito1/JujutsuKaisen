package techniques;

import models.Combatant;

public class PhysicalStrike implements CursedTechnique {

    @Override public String getName()            { return "Physical Strike"; }
    @Override public String getDescription()     { return "A precise strike with a cursed tool."; }
    @Override public int getCursedEnergyCost()   { return 0; }
    @Override public int getCooldown()           { return 0; }
    @Override public int getRange()              { return 1; }
    @Override public String getAnimationType()   { return "HIT"; }
    @Override public boolean canTriggerBlackFlash() { return true; }
    @Override public int getBlackFlashChance()       { return 15; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.6);
        target.takeDamage(damage);
    }
}
