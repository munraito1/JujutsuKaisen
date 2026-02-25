package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.BloodEdge;
import techniques.PiercingBlood;

public class Choso extends SpecialCurse {

    public Choso() {
        super("Тёсо", 170, 35, 18, 24, 140,
                "Кровь утробы");
        addTechnique(new BloodEdge());
        addTechnique(new PiercingBlood());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 30;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.4);
            target.takeDamage(damage);
        }
    }

    @Override public int getBasicAttackBlackFlashChance() { return 10; }

    @Override public int getAbilityCost()    { return 30; }
    @Override public String getAbilityName() { return "Piercing Blood"; }

    @Override
    public int getAttackRange() { return 2; }
}
