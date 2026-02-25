package techniques;

import models.Combatant;

public class BlackFlash implements CursedTechnique {

    @Override public String getName() { return "Чёрная молния"; }
    @Override public String getDescription() { return "Пространственное искажение — удар с множителем 2.5."; }
    @Override public int getCursedEnergyCost() { return 25; }
    @Override public int getCooldown() { return 3; }
    @Override public int getRange() { return 1; }

    @Override
    public String getAnimationType() { return "BLACK_FLASH"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 2.5);
        target.takeDamage(damage);
    }
}
