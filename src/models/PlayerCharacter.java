package models;

public class PlayerCharacter {

    private final String name;
    private final String title;
    private int hp;
    private final int maxHp;
    private int cursedEnergy;
    private final int maxCursedEnergy;
    private final int level;
    private final int attack;
    private final int defense;
    private final int speed;
    private final int moveRange;
    private final int attackRange;

    public PlayerCharacter(String name, String title,
                           int maxHp, int maxCursedEnergy, int level,
                           int attack, int defense, int speed,
                           int moveRange, int attackRange) {
        this.name          = name;
        this.title         = title;
        this.hp            = maxHp;
        this.maxHp         = maxHp;
        this.cursedEnergy  = maxCursedEnergy;
        this.maxCursedEnergy = maxCursedEnergy;
        this.level         = level;
        this.attack        = attack;
        this.defense       = defense;
        this.speed         = speed;
        this.moveRange     = moveRange;
        this.attackRange   = attackRange;
    }

    public String getName()            { return name; }
    public String getTitle()           { return title; }
    public int getHp()                 { return hp; }
    public int getMaxHp()              { return maxHp; }
    public int getCursedEnergy()       { return cursedEnergy; }
    public int getMaxCursedEnergy()    { return maxCursedEnergy; }
    public int getLevel()              { return level; }
    public int getAttack()             { return attack; }
    public int getDefense()            { return defense; }
    public int getSpeed()              { return speed; }
    public int getMoveRange()          { return moveRange; }
    public int getAttackRange()        { return attackRange; }
}
