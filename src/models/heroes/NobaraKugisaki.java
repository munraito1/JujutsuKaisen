package models.heroes;

import enums.Grade;
import models.Combatant;
import models.NamedSorcerer;
import techniques.HairpinTechnique;
import techniques.ResonanceTechnique;

/**
 * Nobara Kugisaki - Straw Doll Technique user, mid-range fighter.
 * Balanced stats with Resonance burst ability.
 */
public class NobaraKugisaki extends NamedSorcerer {

    private boolean resonanceReady;

    public NobaraKugisaki() {
        super("Nobara Kugisaki", "Straw Doll Technique User",
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
            System.out.printf("%s uses Hairpin on %s for %d raw damage!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }

    @Override
    public int getAbilityCost() { return 18; }

    @Override
    public String getAbilityName() { return "Hairpin"; }

    @Override
    public int getAttackRange() { return 2; }

    /**
     * Resonance - powerful follow-up attack, single use until next Hairpin.
     */
    public void useResonance(Combatant target) {
        if (!resonanceReady) {
            System.out.printf("%s's Resonance is not ready!%n", getName());
            return;
        }
        int cost = 25;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.0);
            target.takeDamage(damage);
            resonanceReady = false;
            System.out.printf("%s uses Resonance on %s for %d raw damage!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }

    public boolean isResonanceReady() { return resonanceReady; }
}
