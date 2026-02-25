package techniques;

import models.Combatant;

public class StopTechnique implements CursedTechnique {

    @Override public String getName()            { return "Stop"; }
    @Override public String getDescription()     { return "Voice command: 'Stop!' — disrupts the target's movements."; }
    @Override public int getCursedEnergyCost()   { return 15; }
    @Override public int getCooldown()           { return 0; }
    @Override public int getRange()              { return 4; }
    @Override public String getAnimationType()   { return "HIT"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.4);
        target.takeDamage(damage);
    }
}
