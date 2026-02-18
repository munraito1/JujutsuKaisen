package techniques;

import models.Combatant;

public class NueStrike implements CursedTechnique {

    @Override public String getName() { return "Nue Strike"; }
    @Override public String getDescription() { return "Electric shikigami dive attack from above."; }
    @Override public int getCursedEnergyCost() { return 25; }
    @Override public int getCooldown() { return 2; }
    @Override public int getRange() { return 3; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.3);
        target.takeDamage(damage);
    }
}
