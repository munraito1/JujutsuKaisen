package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.VolcanicAttackTechnique;

/**
 * Jogo - Special Grade cursed spirit born from humanity's fear of fire/volcanoes.
 * High attack power, fire-based abilities.
 * Domain: Coffin of the Iron Mountain.
 */
public class Jogo extends SpecialCurse {

    public Jogo() {
        super("Jogo", 180, 40, 15, 25, 140,
                "Coffin of the Iron Mountain");
        addTechnique(new VolcanicAttackTechnique());
    }

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
            System.out.printf("%s launches a Volcanic Attack on %s for %d raw damage!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }
}
