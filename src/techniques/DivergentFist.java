package techniques;

import models.Combatant;

public class DivergentFist implements CursedTechnique {

    @Override public String getName() { return "Divergent Fist"; }
    @Override public String getDescription() { return "Delayed cursed energy impact on contact."; }
    @Override public int getCursedEnergyCost() { return 15; }
    @Override public int getCooldown() { return 0; }
    @Override public int getRange()                  { return 1; }
    @Override public boolean canTriggerBlackFlash()  { return true; }
    @Override public int getBlackFlashChance()        { return 20; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.8);
        target.takeDamage(damage);
    }
}
