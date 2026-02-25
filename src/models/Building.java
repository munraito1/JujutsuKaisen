package models;

public abstract class Building {

    private final String name;
    private final String description;
    private final int baseCost;
    private int level;
    private final int maxLevel;
    private boolean built;

    public Building(String name, String description, int baseCost, int maxLevel) {
        this.name = name;
        this.description = description;
        this.baseCost = baseCost;
        this.maxLevel = maxLevel;
        this.level = 0;
        this.built = false;
    }

    public double getHealBonusPct(boolean atBase) { return 0.0; }

    public int getYuanIncomeBonus() { return 0; }

    public double getSpawnChanceMultiplier() { return 1.0; }

    public void onBuilt(SorcererTeam playerTeam) {}

    public void onUpgraded(SorcererTeam playerTeam) {}

    public void build() {
        this.built = true;
        this.level = 1;
    }

    public void upgrade() {
        if (canUpgrade()) this.level++;
    }

    public boolean canUpgrade() { return built && level < maxLevel; }

    public int getUpgradeCost() {
        return (int) (baseCost * 1.5 * level);
    }

    public abstract String getEffectDescription();

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getBaseCost() { return baseCost; }
    public int getLevel() { return level; }
    public int getMaxLevel() { return maxLevel; }
    public boolean isBuilt() { return built; }

    @Override
    public String toString() {
        return built
                ? String.format("%s [Lv.%d/%d]", name, level, maxLevel)
                : String.format("%s [Not Built]", name);
    }
}
