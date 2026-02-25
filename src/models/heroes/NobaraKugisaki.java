package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.HairpinTechnique;
import techniques.ResonanceTechnique;

public class NobaraKugisaki extends NamedSorcerer {

    private boolean resonanceReady;

    public NobaraKugisaki() {
        super("Нобара Кугисаки", "Пользователь техники соломенной куклы",
                95, 28, 14, 22, 90, Grade.GRADE_3);
        this.resonanceReady = true;
        addTechnique(new HairpinTechnique());
        addTechnique(new ResonanceTechnique());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 18;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 1.6);
            target.takeDamage(damage);
            resonanceReady = true;
        }
    }

    @Override public int getBasicAttackBlackFlashChance() { return 7; }

    @Override
    public int getAbilityCost() { return 18; }

    @Override
    public String getAbilityName() { return "Hairpin"; }

    @Override
    public int getAttackRange() { return 2; }

    public void useResonance(Combatant target) {
        if (!resonanceReady) {
            return;
        }
        int cost = 25;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.0);
            target.takeDamage(damage);
            resonanceReady = false;
        }
    }

    public boolean isResonanceReady() { return resonanceReady; }
}
