package techniques;

import models.Combatant;

public class ResonanceTechnique implements CursedTechnique {

    @Override public String getName() { return "Resonance"; }
    @Override public String getDescription() { return "Straw Doll link — devastating curse at range."; }
    @Override public int getCursedEnergyCost() { return 25; }
    @Override public int getCooldown() { return 2; }
    @Override public int getRange() { return 3; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.0);
        target.takeDamage(damage);
    }
}
