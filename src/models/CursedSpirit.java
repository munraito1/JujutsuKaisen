package models;

import enums.Grade;

/**
 * Abstract base class for cursed spirits (enemies).
 * Has its own cursed energy pool and a curse grade.
 */
public abstract class CursedSpirit extends Combatant {

    private int cursedEnergy;
    private int maxCursedEnergy;
    private Grade curseGrade;

    public CursedSpirit(String name, int maxHp, int attack, int defense, int speed,
                        int maxCursedEnergy, Grade curseGrade) {
        super(name, maxHp, attack, defense, speed);
        this.maxCursedEnergy = maxCursedEnergy;
        this.cursedEnergy = maxCursedEnergy;
        this.curseGrade = curseGrade;
    }

    public boolean useCursedEnergy(int amount) {
        if (cursedEnergy >= amount) {
            cursedEnergy -= amount;
            return true;
        }
        return false;
    }

    public void restoreCursedEnergy(int amount) {
        cursedEnergy = Math.min(maxCursedEnergy, cursedEnergy + amount);
    }

    // --- Getters ---

    public int getCursedEnergy() { return cursedEnergy; }
    public int getMaxCursedEnergy() { return maxCursedEnergy; }
    public Grade getCurseGrade() { return curseGrade; }

    // --- Protected setters ---

    protected void setCursedEnergy(int cursedEnergy) { this.cursedEnergy = cursedEnergy; }
    protected void setMaxCursedEnergy(int maxCursedEnergy) { this.maxCursedEnergy = maxCursedEnergy; }

    @Override
    public String toString() {
        return String.format("%s [%s Curse | HP: %d/%d, CE: %d/%d, ATK: %d, DEF: %d, SPD: %d]",
                getName(), curseGrade.getDisplayName(), getHp(), getMaxHp(),
                cursedEnergy, maxCursedEnergy, getAttack(), getDefense(), getSpeed());
    }
}
