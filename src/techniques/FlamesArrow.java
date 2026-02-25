package techniques;

import models.Combatant;

public class FlamesArrow implements CursedTechnique {

    @Override public String getName()            { return "Flames Arrow"; }
    @Override public String getDescription()     { return "Sukuna fires a devastating arrow of cursed flames."; }
    @Override public int getCursedEnergyCost()   { return 35; }
    @Override public int getCooldown()           { return 2; }
    @Override public int getRange()              { return 5; }
    @Override public String getAnimationType()   { return "BLACK_FLASH"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.8);
        target.takeDamage(damage);
    }
}
