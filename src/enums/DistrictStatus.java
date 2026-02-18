package enums;

/**
 * Status of a district on the world map.
 */
public enum DistrictStatus {
    LOCKED("Locked"),
    HOSTILE("Hostile"),
    CONTESTED("Contested"),
    CONTROLLED("Controlled");

    private final String displayName;

    DistrictStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
