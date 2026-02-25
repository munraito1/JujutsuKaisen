package techniques;

import models.Combatant;

public class CursedSpeechTechnique implements CursedTechnique {

    @Override public String getName()            { return "Cursed Speech"; }
    @Override public String getDescription()     { return "Voice command infused with cursed energy — 'Explode!'."; }
    @Override public int getCursedEnergyCost()   { return 30; }
    @Override public int getCooldown()           { return 1; }
    @Override public int getRange()              { return 4; }
    @Override public String getAnimationType()   { return "BLACK_FLASH"; }

    @Override
    public void execute(Combatant user, Combatant target) {
        
        int damage = (int) (user.getAttack() * 2.2);
        target.takeDamage(damage);
    }
}
