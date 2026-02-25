package techniques;

import models.Combatant;

public class CleaveSlash implements CursedTechnique {

    @Override public String getName()            { return "Рассечение"; }
    @Override public String getDescription()     { return "Удар, разрезающий всё на своём пути."; }
    @Override public int getCursedEnergyCost()   { return 20; }
    @Override public int getCooldown()           { return 0; }
    @Override public int getRange()              { return 2; }
    @Override public String getAnimationType()   { return "BLACK_FLASH"; }
    @Override public boolean canTriggerBlackFlash() { return true; }
    @Override public int getBlackFlashChance()       { return 20; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.2);
        target.takeDamage(damage);
    }
}
