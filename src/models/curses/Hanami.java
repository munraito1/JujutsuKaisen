package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.DisasterPlantsTechnique;

public class Hanami extends SpecialCurse {

    public Hanami() {
        super("Ханами", 220, 22, 28, 18, 130,
                "Владение шамана");
        addTechnique(new DisasterPlantsTechnique());
    }

    @Override public int getBasicAttackBlackFlashChance() { return 5; }

    @Override
    public int getAbilityCost() { return 25; }

    @Override
    public String getAbilityName() { return "Disaster Plants"; }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 25;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 1.5);
            target.takeDamage(damage);
            heal(15);
        }
    }
}
