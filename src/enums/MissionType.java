package enums;

public enum MissionType {

    EXTERMINATION(
            "Уничтожение",
            "Уничтожьте всех проклятий в районе.",
            "EXTERM"),

    DEFENSE(
            "Оборона",
            "Защитите район от нападения проклятий! Не дайте им укрепиться.",
            "DEF"),

    RESCUE(
            "Спасение",
            "Спасите мирных жителей! Уничтожьте всех врагов до истечения времени.",
            "RESCUE");

    private final String displayName;
    private final String description;
    private final String shortCode;

    MissionType(String displayName, String description, String shortCode) {
        this.displayName = displayName;
        this.description = description;
        this.shortCode   = shortCode;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getShortCode()   { return shortCode; }
}
