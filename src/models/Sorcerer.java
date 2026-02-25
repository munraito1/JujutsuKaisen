package models;

import enums.Grade;

public abstract class Sorcerer extends Combatant {

    private int cursedEnergy;
    private int maxCursedEnergy;
    private Grade grade;

    public Sorcerer(String name, int maxHp, int attack, int defense, int speed,
                    int maxCursedEnergy, Grade grade) {
        super(name, maxHp, attack, defense, speed);
        this.maxCursedEnergy = maxCursedEnergy;
        this.cursedEnergy = maxCursedEnergy;
        this.grade = grade;
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

    public void promoteGrade() {
        Grade[] grades = Grade.values();
        for (int i = 0; i < grades.length - 1; i++) {
            if (grades[i] == grade) {
                grade = grades[i + 1];
                maxCursedEnergy += 20;
                cursedEnergy = maxCursedEnergy;
                break;
            }
        }
    }

    public int getCursedEnergy() { return cursedEnergy; }
    public int getMaxCursedEnergy() { return maxCursedEnergy; }
    public Grade getGrade() { return grade; }

    protected void setCursedEnergy(int cursedEnergy) { this.cursedEnergy = cursedEnergy; }
    protected void setMaxCursedEnergy(int maxCursedEnergy) { this.maxCursedEnergy = maxCursedEnergy; }
    protected void setGrade(Grade grade) { this.grade = grade; }

    @Override
    public String toString() {
        return String.format("%s [%s | HP: %d/%d, CE: %d/%d, ATK: %d, DEF: %d, SPD: %d, LVL: %d]",
                getName(), grade.getDisplayName(), getHp(), getMaxHp(),
                cursedEnergy, maxCursedEnergy, getAttack(), getDefense(), getSpeed(), getLevel());
    }
}
