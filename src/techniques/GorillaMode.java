package techniques;

import models.Combatant;

public class GorillaMode implements CursedTechnique {

    @Override public String getName()            { return "Gorilla Mode"; }
    @Override public String getDescription()     { return "Activates the gorilla core — devastating power strike."; }
    @Override public int getCursedEnergyCost()   { return 25; }
    @Override public int getCooldown()           { return 2; }
    @Override public int getRange()              { return 1; }
    @Override public String getAnimationType()   { return "BLACK_FLASH"; }
    @Override public boolean canTriggerBlackFlash() { return true; }
    @Override public int getBlackFlashChance()       { return 25; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.5);
        target.takeDamage(damage);
    }
}
