package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.InfinityBarrier;
import techniques.InfiniteVoidTechnique;

public class SatoruGojo extends NamedSorcerer {

    public SatoruGojo() {
        super("Сатору Годзё", "Сильнейший",
                180, 55, 35, 38, 200, Grade.SPECIAL_GRADE);
        addTechnique(new InfinityBarrier());
        addTechnique(new InfiniteVoidTechnique());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 60;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 3.5);
            target.takeDamage(damage);
        }
    }

    @Override
    public void levelUp() {
        super.levelUp();
        setAttack(getAttack() + 3);
        setMaxCursedEnergy(getMaxCursedEnergy() + 20);
    }

    @Override
    public int getAttackRange() { return 2; } 

    @Override public int getBasicAttackBlackFlashChance() { return 12; }

    @Override public int getAbilityCost()    { return 60; }
    @Override public String getAbilityName() { return "Infinite Void"; }
}
