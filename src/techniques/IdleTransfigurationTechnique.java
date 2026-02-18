package techniques;

import models.Combatant;

public class IdleTransfigurationTechnique implements CursedTechnique {

    @Override public String getName() { return "Idle Transfiguration"; }
    @Override public String getDescription() { return "Reshape the target's soul to deal damage."; }
    @Override public int getCursedEnergyCost() { return 30; }
    @Override public int getCooldown() { return 1; }
    @Override public int getRange() { return 1; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.0);
        target.takeDamage(damage);
    }
}
