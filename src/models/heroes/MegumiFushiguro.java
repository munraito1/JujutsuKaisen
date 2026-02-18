package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.DivineDogs;
import techniques.NueStrike;

/**
 * Megumi Fushiguro - Ten Shadows Technique user, summons shikigami.
 * Balanced stats, high cursed energy pool.
 */
public class MegumiFushiguro extends NamedSorcerer {

    private int activeShikigami;
    private static final int MAX_SHIKIGAMI = 2;

    public MegumiFushiguro() {
        super("Megumi Fushiguro", "Ten Shadows Technique User",
                100, 25, 18, 25, 100, Grade.GRADE_2);
        this.activeShikigami = 0;
        addTechnique(new DivineDogs());
        addTechnique(new NueStrike());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 20;
        if (activeShikigami >= MAX_SHIKIGAMI) {
            System.out.printf("%s already has maximum shikigami summoned!%n", getName());
            return;
        }
        if (useCursedEnergy(cost)) {
            activeShikigami++;
            int damage = (int) (getAttack() * 1.5);
            target.takeDamage(damage);
            System.out.printf("%s summons Divine Dogs to attack %s for %d raw damage!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }

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
        setMaxCursedEnergy(getMaxCursedEnergy() + 10); // bonus CE growth
    }
}
