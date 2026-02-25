package techniques;

import models.Combatant;

public class BloodEdge implements CursedTechnique {

    @Override public String getName()            { return "Кровяной клинок"; }
    @Override public String getDescription()     { return "Затвердевшая кровь превращается в острый клинок."; }
    @Override public int getCursedEnergyCost()   { return 18; }
    @Override public int getCooldown()           { return 0; }
    @Override public int getRange()              { return 1; }
    @Override public String getAnimationType()   { return "HIT"; }
    @Override public boolean canTriggerBlackFlash() { return true; }
    @Override public int getBlackFlashChance()       { return 10; }

    @Override
    public void execute(Combatant user, Combatant target) {
        int damage = (int) (user.getAttack() * 1.9);
        target.takeDamage(damage);
    }
}
