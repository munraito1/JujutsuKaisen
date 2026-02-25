package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.CursedSpeechTechnique;
import techniques.StopTechnique;

public class InumakiToge extends NamedSorcerer {

    public InumakiToge() {
        super("Тогэ Инумаки", "Змеиные глаза и клыки",
                85, 18, 12, 20, 120, Grade.GRADE_2);
        addTechnique(new StopTechnique());
        addTechnique(new CursedSpeechTechnique());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 30;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.2);
            target.takeDamage(damage);
        }
    }

    @Override
    public void levelUp() {
        super.levelUp();
        setMaxCursedEnergy(getMaxCursedEnergy() + 15); 
    }

    @Override public int getBasicAttackBlackFlashChance() { return 3; }

    @Override public int getAbilityCost()    { return 30; }
    @Override public String getAbilityName() { return "Cursed Speech: Explode"; }
}
