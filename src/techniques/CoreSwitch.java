package techniques;

import models.Combatant;

public class CoreSwitch implements CursedTechnique {

    @Override public String getName()            { return "Core Switch"; }
    @Override public String getDescription()     { return "Switches to a different cursed corpse core — restores HP."; }
    @Override public int getCursedEnergyCost()   { return 20; }
    @Override public int getCooldown()           { return 3; }
    @Override public int getRange()              { return 1; }
    @Override public String getAnimationType()   { return "NONE"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        
        int healAmount = (int) (user.getMaxHp() * 0.35);
        user.heal(healAmount);
        
        int damage = (int) (user.getAttack() * 0.5);
        target.takeDamage(damage);
    }
}
