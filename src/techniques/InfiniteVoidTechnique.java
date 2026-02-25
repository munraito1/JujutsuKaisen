package techniques;

import models.Combatant;

public class InfiniteVoidTechnique implements CursedTechnique {

    @Override public String getName()            { return "Infinite Void"; }
    @Override public String getDescription()     { return "Domain Expansion: Infinite Void — infinite sensory overload."; }
    @Override public int getCursedEnergyCost()   { return 60; }
    @Override public int getCooldown()           { return 4; }
    @Override public int getRange()              { return 5; }
    @Override public String getAnimationType()   { return "BLACK_FLASH"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 3.5);
        target.takeDamage(damage);
    }
}
