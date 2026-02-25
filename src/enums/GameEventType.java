package enums;

public enum GameEventType {

    CURSED_OUTBREAK(
            "Вспышка проклятий",
            "Внезапный всплеск активности проклятых духов!"),

    SPECIAL_GRADE_SIGHTING(
            "Замечен особый ранг",
            "Разведка сообщает об особо опасном проклятии!"),

    CIVILIAN_IN_DANGER(
            "Гражданский в опасности",
            "Мирные жители попали в ловушку! Требуется срочное вмешательство."),

    DOMAIN_EMERGENCY(
            "Экстренное расширение домена",
            "В районе разворачивается домен! Немедленно нейтрализуйте угрозу!"),

    REINFORCEMENTS(
            "Прибыло подкрепление",
            "К команде присоединяется отряд поддержки магов.");

    private final String displayName;
    private final String description;

    GameEventType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
