package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.BlackFlash;
import techniques.DivergentFist;

/**
 * Yuji Itadori - physical brawler and vessel of Sukuna.
 * High HP, high attack, high speed. Signature: Divergent Fist.
 */
public class YujiItadori extends NamedSorcerer {

    private int divergentFistCharge;

    public YujiItadori() {
        super("Yuji Itadori", "Vessel of Sukuna",
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
            System.out.printf("%s uses Divergent Fist on %s for %d raw damage!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }

    @Override
    public void levelUp() {
        super.levelUp();
        setAttack(getAttack() + 3); // bonus physical growth
    }

    @Override
    public int getAbilityCost() { return 15; }

    @Override
    public String getAbilityName() { return "Divergent Fist"; }

    public int getDivergentFistCharge() { return divergentFistCharge; }

    public void chargeDivergentFist() { divergentFistCharge++; }
}
