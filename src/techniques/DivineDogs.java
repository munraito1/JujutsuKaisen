package techniques;

import models.Combatant;

public class DivineDogs implements CursedTechnique {

    @Override public String getName() { return "Divine Dogs"; }
    @Override public String getDescription() { return "Summon shikigami wolves to attack a target."; }
    @Override public int getCursedEnergyCost() { return 20; }
    @Override public int getCooldown() { return 1; }
    @Override public int getRange() { return 2; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.5);
        target.takeDamage(damage);
    }
}
