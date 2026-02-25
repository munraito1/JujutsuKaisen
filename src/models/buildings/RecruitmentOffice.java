package models.buildings;

import models.Building;
import models.SorcererTeam;

public class RecruitmentOffice extends Building {

    public RecruitmentOffice() {
        super("Приёмная комиссия",
                "Организует найм новых магов в команду. Открывает уникальных героев.",
                60, 3);
    }

    @Override
    public String getEffectDescription() {
        if (!isBuilt()) return "Не построено.";
        return switch (getLevel()) {
            case 1 -> "Доступно: Maki Zenin (5 ОР)";
            case 2 -> "Доступно: Maki, Inumaki (8 ОР), Panda (8 ОР)";
            case 3 -> "Доступно: Maki, Inumaki, Panda, Satoru Gojo (20 ОР)";
            default -> "";
        };
    }
}
