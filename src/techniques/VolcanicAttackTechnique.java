package techniques;

import models.Combatant;

public class VolcanicAttackTechnique implements CursedTechnique {

    @Override public String getName() { return "Volcanic Attack"; }
    @Override public String getDescription() { return "Unleash searing volcanic flames at range."; }
    @Override public int getCursedEnergyCost() { return 35; }
    @Override public int getCooldown() { return 2; }
    @Override public int getRange() { return 3; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.2);
        target.takeDamage(damage);
    }
}
