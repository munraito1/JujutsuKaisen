package controllers;

import models.Building;
import models.SorcererTeam;
import models.buildings.Archive;
import models.buildings.BarrierStation;
import models.buildings.Dojo;
import models.buildings.MedicalWing;
import models.buildings.RecruitmentOffice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuildingManager {

    private final List<Building> allBuildings;

    public BuildingManager() {
        allBuildings = new ArrayList<>();
        allBuildings.add(new MedicalWing());
        allBuildings.add(new Dojo());
        allBuildings.add(new Archive());
        allBuildings.add(new BarrierStation());
        allBuildings.add(new RecruitmentOffice());
    }

    public boolean build(Building building, GameManager gm) {
        if (building.isBuilt()) return false;
        if (!gm.spendYuan(building.getBaseCost())) return false;

        building.build();
        building.onBuilt(gm.getPlayerTeam());
        return true;
    }

    public boolean upgrade(Building building, GameManager gm) {
        if (!building.canUpgrade()) return false;
        if (!gm.spendYuan(building.getUpgradeCost())) return false;

        building.upgrade();
        building.onUpgraded(gm.getPlayerTeam());
        return true;
    }

    public double getTotalHealBonus(boolean atBase) {
        return allBuildings.stream()
                .mapToDouble(b -> b.getHealBonusPct(atBase))
                .sum();
    }

    public int getTotalYuanIncome() {
        return allBuildings.stream()
                .mapToInt(Building::getYuanIncomeBonus)
                .sum();
    }

    public double getSpawnChanceMultiplier() {
        return allBuildings.stream()
                .mapToDouble(Building::getSpawnChanceMultiplier)
                .reduce(1.0, (a, b) -> a * b);
    }

    public int getRecruitmentOfficeLevel() {
        return allBuildings.stream()
                .filter(b -> b instanceof RecruitmentOffice)
                .mapToInt(b -> b.isBuilt() ? b.getLevel() : 0)
                .findFirst().orElse(0);
    }

    public List<Building> getAllBuildings() { return allBuildings; }

    public List<Building> getBuiltBuildings() {
        return allBuildings.stream().filter(Building::isBuilt).collect(Collectors.toList());
    }

    public List<Building> getAvailableBuildings() {
        return allBuildings.stream().filter(b -> !b.isBuilt()).collect(Collectors.toList());
    }
}
