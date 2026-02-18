package models;

/**
 * Abstract base class for all combat participants in the game.
 * Provides core stats: HP, attack, defense, speed, level.
 */
public abstract class Combatant {

    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int level;
    private boolean alive;

    public Combatant(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.level = 1;
        this.alive = true;
    }

    /**
     * Apply damage to this combatant. Damage is reduced by defense (minimum 1).
     */
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        hp -= actualDamage;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    /**
     * Heal this combatant. Cannot exceed max HP.
     */
    public void heal(int amount) {
        if (!alive && amount > 0) {
            return; // cannot heal the dead
        }
        hp = Math.min(maxHp, hp + amount);
    }

    /**
     * Level up: increase base stats.
     */
    public void levelUp() {
        level++;
        maxHp += 10;
        hp = maxHp;
        attack += 2;
        defense += 1;
        speed += 1;
    }

    // --- Getters ---

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public int getLevel() { return level; }
    public boolean isAlive() { return alive; }

    // --- Protected setters for subclasses ---

    protected void setHp(int hp) { this.hp = Math.min(maxHp, Math.max(0, hp)); }
    protected void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    protected void setAttack(int attack) { this.attack = attack; }
    protected void setDefense(int defense) { this.defense = defense; }
    protected void setSpeed(int speed) { this.speed = speed; }
    protected void setLevel(int level) { this.level = level; }
    protected void setAlive(boolean alive) { this.alive = alive; }

    /**
     * Movement range on battle grid (tiles per turn). Derived from speed.
     */
    public int getMovementRange() {
        return Math.max(2, getSpeed() / 10);
    }

    /**
     * Basic attack range (Chebyshev distance). 1 = melee.
     */
    public int getAttackRange() {
        return 1;
    }

    /**
     * Return a detailed multi-line description of this combatant.
     */
    public abstract String getInfo();

    @Override
    public String toString() {
        return String.format("%s [HP: %d/%d, ATK: %d, DEF: %d, SPD: %d, LVL: %d]",
                name, hp, maxHp, attack, defense, speed, level);
    }
}
