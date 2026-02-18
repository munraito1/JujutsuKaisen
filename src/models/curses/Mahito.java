package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.IdleTransfigurationTechnique;

/**
 * Mahito - Special Grade cursed spirit.
 * Idle Transfiguration: manipulates the shape of souls.
 * Domain: Self-Embodiment of Perfection.
 */
public class Mahito extends SpecialCurse {

    private boolean idleTransfigurationActive;

    public Mahito() {
        super("Mahito", 200, 30, 20, 22, 150,
                "Self-Embodiment of Perfection");
        this.idleTransfigurationActive = false;
        addTechnique(new IdleTransfigurationTechnique());
    }

    @Override
    public void useSpecialAbility(Combatant target) {
        int cost = 30;
        if (useCursedEnergy(cost)) {
            int damage = (int) (getAttack() * 2.0);
            target.takeDamage(damage);
            idleTransfigurationActive = true;
            System.out.printf("%s uses Idle Transfiguration on %s for %d raw damage!%n",
                    getName(), target.getName(), damage);
        } else {
            System.out.printf("%s doesn't have enough cursed energy!%n", getName());
        }
    }

    @Override
    public int getAbilityCost() { return 30; }

    @Override
    public String getAbilityName() { return "Idle Transfiguration"; }

    public boolean isIdleTransfigurationActive() { return idleTransfigurationActive; }
}
