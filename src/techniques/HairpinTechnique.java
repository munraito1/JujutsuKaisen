package techniques;

import models.Combatant;

public class HairpinTechnique implements CursedTechnique {

    @Override public String getName() { return "Hairpin"; }
    @Override public String getDescription() { return "Infuse and launch nails with cursed energy."; }
    @Override public int getCursedEnergyCost() { return 18; }
    @Override public int getCooldown() { return 0; }
    @Override public int getRange() { return 2; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.6);
        target.takeDamage(damage);
    }
}
