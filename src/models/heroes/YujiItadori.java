package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.BlackFlash;
import techniques.DivergentFist;

public class YujiItadori extends NamedSorcerer {

    private int divergentFistCharge;

    public YujiItadori() {
        super("Юдзи Итадори", "Сосуд Сукуны",
                120, 35, 15, 30, 80, Grade.GRADE_4);
        this.divergentFistCharge = 0;
        addTechnique(new DivergentFist());
        addTechnique(new BlackFlash());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 15;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 1.8);
            target.takeDamage(damage);
            divergentFistCharge = 0;
        }
    }

    @Override
    public void levelUp() {
        super.levelUp();
        setAttack(getAttack() + 3); 
    }

    @Override public int getBasicAttackBlackFlashChance() { return 25; }

    @Override
    public int getAbilityCost() { return 15; }

    @Override
    public String getAbilityName() { return "Divergent Fist"; }

    public int getDivergentFistCharge() { return divergentFistCharge; }

    public void chargeDivergentFist() { divergentFistCharge++; }
}
