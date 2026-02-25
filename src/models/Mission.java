package models;

import enums.MissionType;

public class Mission {

    private final MissionType type;
    private final int turnLimit;          
    private final double rewardMultiplier;

    private Mission(MissionType type, int turnLimit, double rewardMultiplier) {
        this.type             = type;
        this.turnLimit        = turnLimit;
        this.rewardMultiplier = rewardMultiplier;
    }

    public static Mission extermination() {
        return new Mission(MissionType.EXTERMINATION, 0, 1.0);
    }

    public static Mission defense() {
        return new Mission(MissionType.DEFENSE, 0, 1.2);
    }

    public static Mission rescue(int turns) {
        return new Mission(MissionType.RESCUE, turns, 1.5);
    }

    public MissionType getType()             { return type; }
    public int         getTurnLimit()        { return turnLimit; }
    public boolean     hasTurnLimit()        { return turnLimit > 0; }
    public double      getRewardMultiplier() { return rewardMultiplier; }

    public String getDisplayName() { return type.getDisplayName(); }

    @Override
    public String toString() {
        String base = type.getDisplayName();
        if (hasTurnLimit()) base += " (" + turnLimit + " ходов)";
        return base;
    }
}
