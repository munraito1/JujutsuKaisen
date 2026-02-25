package techniques;

import models.Combatant;

public class PiercingBlood implements CursedTechnique {

    @Override public String getName()            { return "Piercing Blood"; }
    @Override public String getDescription()     { return "A high-speed blood jet that pierces through defenses."; }
    @Override public int getCursedEnergyCost()   { return 30; }
    @Override public int getCooldown()           { return 1; }
    @Override public int getRange()              { return 6; }
    @Override public String getAnimationType()   { return "BLACK_FLASH"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.4);
        target.takeDamage(damage);
    }
}
