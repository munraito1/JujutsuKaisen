package models;

public abstract class Combatant {

    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int level;
    private boolean alive;
    private int experience;
    private int expToNextLevel;

    public Combatant(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.level = 1;
        this.alive = true;
        this.experience = 0;
        this.expToNextLevel = 100;
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        hp -= actualDamage;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    public void heal(int amount) {
        if (!alive && amount > 0) {
            return; 
        }
        hp = Math.min(maxHp, hp + amount);
    }

    public void levelUp() {
        level++;
        maxHp += 10;
        hp = maxHp;
        attack += 2;
        defense += 1;
        speed += 1;
        expToNextLevel = level * 100;
    }

    public int addExperience(int xp) {
        if (xp <= 0) return 0;
        experience += xp;
        int levelsGained = 0;
        while (experience >= expToNextLevel) {
            experience -= expToNextLevel;
            levelUp();
            levelsGained++;
        }
        return levelsGained;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public int getLevel() { return level; }
    public boolean isAlive() { return alive; }
    public int getExperience() { return experience; }
    public int getExpToNextLevel() { return expToNextLevel; }

    protected void setHp(int hp) { this.hp = Math.min(maxHp, Math.max(0, hp)); }
    protected void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    protected void setAttack(int attack) { this.attack = attack; }
    protected void setDefense(int defense) { this.defense = defense; }
    protected void setSpeed(int speed) { this.speed = speed; }
    protected void setLevel(int level) { this.level = level; }
    protected void setAlive(boolean alive) { this.alive = alive; }

    public int getBasicAttackBlackFlashChance() { return 5; }

    public int getMovementRange() {
        return Math.max(2, getSpeed() / 10);
    }

    public int getAttackRange() {
        return 1;
    }

    public abstract String getInfo();

    @Override
    public String toString() {
        return String.format("%s [HP: %d/%d, ATK: %d, DEF: %d, SPD: %d, LVL: %d]",
                name, hp, maxHp, attack, defense, speed, level);
    }
}
