package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.VolcanicAttackTechnique;

public class Jogo extends SpecialCurse {

    public Jogo() {
        super("Дзёго", 180, 40, 15, 25, 140,
                "Гроб железной горы");
        addTechnique(new VolcanicAttackTechnique());
    }

    @Override public int getBasicAttackBlackFlashChance() { return 5; }

    @Override
    public int getAbilityCost() { return 35; }

    @Override
    public String getAbilityName() { return "Volcanic Attack"; }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 35;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.2);
            target.takeDamage(damage);
        }
    }
}
