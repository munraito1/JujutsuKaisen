package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.PhysicalStrike;
import techniques.SpearThrust;

public class MakiZenin extends NamedSorcerer {

    public MakiZenin() {
        super("Маки Зенин", "Отступница клана Зенин",
                110, 40, 22, 28, 0, Grade.GRADE_4);
        addTechnique(new PhysicalStrike());
        addTechnique(new SpearThrust());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        
        int damage = (int) (getAttack() * 2.0);
        target.takeDamage(damage);
    }

    @Override
    public void levelUp() {
        super.levelUp();
        setAttack(getAttack() + 4); 
        setDefense(getDefense() + 2);
    }

    @Override public int getBasicAttackBlackFlashChance() { return 22; }

    @Override public int getAbilityCost()   { return 0; }
    @Override public String getAbilityName() { return "Special Grade Tool Strike"; }
}
