package models;

public enum Technology {

    BASIC_COMBAT(
            "Базовый бой",
            "Фундаментальные боевые техники. +5% к урону в бою.",
            0, null, "COMBAT"),

    ADVANCED_COMBAT(
            "Продвинутый бой",
            "Совершенствование атак и стоек. +10% к урону в бою.",
            2, BASIC_COMBAT, "COMBAT"),

    MASTER_COMBAT(
            "Мастерство боя",
            "Безупречное владение телом и техникой. +15% к урону в бою.",
            5, ADVANCED_COMBAT, "COMBAT"),

    CE_EFFICIENCY(
            "Оптимизация ПЭ",
            "Рациональное расходование проклятой энергии. Стоимость техник -15%.",
            2, null, "CE"),

    CE_MASTERY(
            "Мастерство ПЭ",
            "Полный контроль над потоками энергии. +30 к макс. ПЭ для всей команды.",
            4, CE_EFFICIENCY, "CE"),

    BARRIER_BASICS(
            "Барьерные техники",
            "Охранные барьеры в районах. Шанс появления проклятий -20%.",
            2, null, "BARRIERS"),

    ADVANCED_BARRIERS(
            "Продвинутые барьеры",
            "Многослойные барьеры. Шанс появления проклятий -40%.",
            4, BARRIER_BASICS, "BARRIERS"),

    FIELD_MEDICINE(
            "Полевая медицина",
            "Лечение в полевых условиях. +10% к исцелению вне базы.",
            3, null, "MEDICINE");

    private final String displayName;
    private final String description;
    private final int cost; 
    private final Technology prerequisite;
    private final String path;

    Technology(String displayName, String description, int cost,
               Technology prerequisite, String path) {
        this.displayName   = displayName;
        this.description   = description;
        this.cost          = cost;
        this.prerequisite  = prerequisite;
        this.path          = path;
    }

    public String getDisplayName()    { return displayName; }
    public String getDescription()    { return description; }
    public int getCost()              { return cost; }
    public Technology getPrerequisite() { return prerequisite; }
    public String getPath()           { return path; }
}
