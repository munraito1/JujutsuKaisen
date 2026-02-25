package techniques;

import models.Combatant;

public class InfinityBarrier implements CursedTechnique {

    @Override public String getName()            { return "Infinity"; }
    @Override public String getDescription()     { return "Limitless technique: infinite subdivision reflects damage."; }
    @Override public int getCursedEnergyCost()   { return 20; }
    @Override public int getCooldown()           { return 1; }
    @Override public int getRange()              { return 2; }
    @Override public String getAnimationType()   { return "HIT"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        
        int damage = (int) (user.getAttack() * 1.9);
        target.takeDamage(damage);
        
        user.heal((int) (user.getMaxHp() * 0.10));
    }
}
