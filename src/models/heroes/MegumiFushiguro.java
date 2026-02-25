package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.DivineDogs;
import techniques.NueStrike;

public class MegumiFushiguro extends NamedSorcerer {

    private int activeShikigami;
    private static final int MAX_SHIKIGAMI = 2;

    public MegumiFushiguro() {
        super("Мегуми Фусигуро", "Пользователь техники десяти теней",
                100, 25, 18, 25, 100, Grade.GRADE_2);
        this.activeShikigami = 0;
        addTechnique(new DivineDogs());
        addTechnique(new NueStrike());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 20;
        if (activeShikigami >= MAX_SHIKIGAMI) {
            return;
        }
        if (useCursedEnergy(cost)) {
            activeShikigami++;
            int damage = (int) (getAttack() * 1.5);
            target.takeDamage(damage);
        }
    }

    @Override public int getBasicAttackBlackFlashChance() { return 8; }

    @Override
    public int getAbilityCost() { return 20; }

    @Override
    public String getAbilityName() { return "Divine Dogs"; }

    public void dismissShikigami() {
        if (activeShikigami > 0) {
            activeShikigami--;
        }
    }

    public int getActiveShikigami() { return activeShikigami; }

    @Override
    public void levelUp() {
        super.levelUp();
        setMaxCursedEnergy(getMaxCursedEnergy() + 10); 
    }
}
