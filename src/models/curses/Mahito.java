package models.curses;

import models.Combatant;
import models.SpecialCurse;
import techniques.IdleTransfigurationTechnique;

public class Mahito extends SpecialCurse {

    private boolean idleTransfigurationActive;

    public Mahito() {
        super("Махито", 200, 30, 20, 22, 150,
                "Воплощение совершенства");
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
        }
    }

    @Override public int getBasicAttackBlackFlashChance() { return 8; }

    @Override
    public int getAbilityCost() { return 30; }

    @Override
    public String getAbilityName() { return "Idle Transfiguration"; }

    public boolean isIdleTransfigurationActive() { return idleTransfigurationActive; }
}
