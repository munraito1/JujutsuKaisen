package models.buildings;

import models.Building;
import models.SorcererTeam;

public class Archive extends Building {

    private static final int[] YUAN_PER_LEVEL = {0, 8, 16, 25};

    public Archive() {
        super("Архив",
                "Исследовательский центр. Пассивно генерирует иены каждый ход.",
                45, 3);
    }

    @Override
    public int getYuanIncomeBonus() {
        if (!isBuilt()) return 0;
        return YUAN_PER_LEVEL[getLevel()];
    }

    @Override
    public String getEffectDescription() {
        if (!isBuilt()) return "Не построено.";
        return "+" + YUAN_PER_LEVEL[getLevel()] + "¥/ход";
    }
}
