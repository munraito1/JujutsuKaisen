package models.buildings;

import models.Building;
import models.SorcererTeam;

public class BarrierStation extends Building {

    public BarrierStation() {
        super("Барьерная станция",
                "Барьерная сеть, подавляющая активность проклятий в контролируемых районах.",
                35, 2);
    }

    @Override
    public double getSpawnChanceMultiplier() {
        if (!isBuilt()) return 1.0;
        switch (getLevel()) {
            case 1: return 0.5;
            case 2: return 0.2;
            default: return 1.0;
        }
    }

    @Override
    public String getEffectDescription() {
        if (!isBuilt()) return "Не построено.";
        switch (getLevel()) {
            case 1: return "Респаун проклятий: 5%/ход (было 10%)";
            case 2: return "Респаун проклятий: 2%/ход (было 10%)";
            default: return "";
        }
    }
}
