package techniques;

import models.Combatant;

public class DisasterPlantsTechnique implements CursedTechnique {

    @Override public String getName() { return "Disaster Plants"; }
    @Override public String getDescription() { return "Cursed plants that damage and drain life."; }
    @Override public int getCursedEnergyCost() { return 25; }
    @Override public int getCooldown() { return 1; }
    @Override public int getRange() { return 2; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.5);
        target.takeDamage(damage);
        user.heal(15);
    }
}
