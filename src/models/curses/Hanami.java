package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.DisasterPlantsTechnique;

/**
 * Hanami - Special Grade cursed spirit born from humanity's fear of nature.
 * Tanky with self-healing. Nature-based attacks.
 * Domain: Shaman's Domain.
 */
public class Hanami extends SpecialCurse {

    public Hanami() {
        super("Hanami", 220, 22, 28, 18, 130,
                "Shaman's Domain");
        addTechnique(new DisasterPlantsTechnique());
    }

    @Override
    public int getAbilityCost() { return 25; }

    @Override
    public String getAbilityName() { return "Disaster Plants"; }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 25;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 1.5);
            target.takeDamage(damage);
            heal(15);
            System.out.printf("%s uses Disaster Plants on %s for %d raw damage and heals 15 HP!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }
}
