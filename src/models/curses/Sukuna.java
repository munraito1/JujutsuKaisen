package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.CleaveSlash;
import techniques.FlamesArrow;

public class Sukuna extends SpecialCurse {

    public Sukuna() {
        super("Рёмэн Сукуна", 350, 55, 30, 32, 200,
                "Злобный храм");
        addTechnique(new CleaveSlash());
        addTechnique(new FlamesArrow());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 35;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.8);
            target.takeDamage(damage);
        }
    }

    @Override public int getBasicAttackBlackFlashChance() { return 28; }

    @Override public int getAbilityCost()    { return 35; }
    @Override public String getAbilityName() { return "Malevolent Shrine"; }

    @Override
    public int getAttackRange() { return 2; }
}
