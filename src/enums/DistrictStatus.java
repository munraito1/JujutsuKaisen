package enums;

public enum DistrictStatus {
    LOCKED("Заблокирован"),
    HOSTILE("Враждебный"),
    CONTESTED("Оспариваемый"),
    CONTROLLED("Под контролем");

    private final String displayName;

    DistrictStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
