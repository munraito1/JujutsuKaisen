package models;

import enums.Grade;

public class GenericSorcererSquad extends Sorcerer {

    private final int squadSize;
    private int membersAlive;
    private final int hpPerMember;

    public GenericSorcererSquad(String name, Grade grade, int squadSize,
                                int hpPerMember, int attack, int defense,
                                int speed, int maxCursedEnergy) {
        super(name, hpPerMember * squadSize, attack, defense, speed, maxCursedEnergy, grade);
        this.squadSize = squadSize;
        this.membersAlive = squadSize;
        this.hpPerMember = hpPerMember;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        membersAlive = Math.max(0, (int) Math.ceil((double) getHp() / hpPerMember));
        if (membersAlive == 0) {
            setAlive(false);
        }
    }

    public int getMembersAlive() { return membersAlive; }
    public int getSquadSize() { return squadSize; }

    @Override
    public String getInfo() {
        return String.format(
                "=== %s ===%nGrade: %s%nMembers: %d/%d%nHP: %d/%d%n" +
                "Attack: %d | Defense: %d | Speed: %d",
                getName(), getGrade().getDisplayName(), membersAlive, squadSize,
                getHp(), getMaxHp(), getAttack(), getDefense(), getSpeed());
    }

    public static GenericSorcererSquad createGrade3Squad() {
        return new GenericSorcererSquad("Grade 3 Sorcerer Squad", Grade.GRADE_3,
                5, 20, 10, 5, 12, 30);
    }

    public static GenericSorcererSquad createGrade2Squad() {
        return new GenericSorcererSquad("Grade 2 Sorcerer Squad", Grade.GRADE_2,
                4, 30, 15, 8, 15, 50);
    }

    public static GenericSorcererSquad createGrade1Squad() {
        return new GenericSorcererSquad("Grade 1 Sorcerer Squad", Grade.GRADE_1,
                3, 45, 22, 12, 18, 70);
    }
}
