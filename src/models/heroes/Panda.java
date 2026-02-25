package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.CoreSwitch;
import techniques.GorillaMode;

public class Panda extends NamedSorcerer {

    public Panda() {
        super("Панда", "Внезапно мутировавший проклятый труп",
                160, 28, 28, 18, 100, Grade.GRADE_2);
        addTechnique(new GorillaMode());
        addTechnique(new CoreSwitch());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 25;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.5);
            target.takeDamage(damage);
        }
    }

    @Override
    public void levelUp() {
        super.levelUp();
        setMaxHp(getMaxHp() + 15); 
        setDefense(getDefense() + 2);
        setHp(getMaxHp()); 
    }

    @Override public int getBasicAttackBlackFlashChance() { return 15; }

    @Override public int getAbilityCost()    { return 25; }
    @Override public String getAbilityName() { return "Gorilla Mode"; }
}
