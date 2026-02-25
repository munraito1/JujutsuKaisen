package models.buildings;

import models.Building;
import models.SorcererTeam;

public class MedicalWing extends Building {

    public MedicalWing() {
        super("Медицинский корпус",
                "Автоматически лечит команду между ходами. "
                + "Высокие уровни увеличивают процент и расширяют действие.",
                50, 3);
    }

    @Override
    public double getHealBonusPct(boolean atBase) {
        if (!isBuilt()) return 0.0;
        switch (getLevel()) {
            case 1: return atBase ? 0.15 : 0.0;
            case 2: return atBase ? 0.25 : 0.0;
            case 3: return 0.30;
            default: return 0.0;
        }
    }

    @Override
    public String getEffectDescription() {
        if (!isBuilt()) return "Не построено.";
        switch (getLevel()) {
            case 1: return "+15% HP за ход на базе (итого 45%)";
            case 2: return "+25% HP за ход на базе (итого 55%)";
            case 3: return "+30% HP за ход везде (итого 60%)";
            default: return "";
        }
    }
}
