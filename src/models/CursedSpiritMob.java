package models;

import enums.Grade;

/**
 * Generic cursed spirit mob (swarm). Acts as a single unit but members die as HP drops.
 */
public class CursedSpiritMob extends CursedSpirit {

    private final int swarmSize;
    private int membersAlive;
    private final int hpPerMember;

    public CursedSpiritMob(String name, Grade grade, int swarmSize,
                           int hpPerMember, int attack, int defense,
                           int speed, int maxCursedEnergy) {
        super(name, hpPerMember * swarmSize, attack, defense, speed, maxCursedEnergy, grade);
        this.swarmSize = swarmSize;
        this.membersAlive = swarmSize;
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

    public int getSwarmSize() { return swarmSize; }
    public int getMembersAlive() { return membersAlive; }

    @Override
    public String getInfo() {
        return String.format(
                "=== %s ===%nGrade: %s%nSwarm: %d/%d%nHP: %d/%d%n" +
                "Attack: %d | Defense: %d | Speed: %d",
                getName(), getCurseGrade().getDisplayName(), membersAlive, swarmSize,
                getHp(), getMaxHp(), getAttack(), getDefense(), getSpeed());
    }

    // --- Factory methods ---

    public static CursedSpiritMob createSwarm() {
        return new CursedSpiritMob("Cursed Spirit Swarm", Grade.GRADE_4,
                8, 10, 6, 3, 8, 10);
    }

    public static CursedSpiritMob createGrade2Pack() {
        return new CursedSpiritMob("Grade 2 Curse Pack", Grade.GRADE_2,
                4, 25, 14, 7, 14, 30);
    }

    public static CursedSpiritMob createGrade1Curse() {
        return new CursedSpiritMob("Grade 1 Curse", Grade.GRADE_1,
                1, 120, 25, 15, 20, 60);
    }
}
