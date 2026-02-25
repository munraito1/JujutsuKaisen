package models;

import controllers.ResourceManager;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TechTree {

    private final Set<Technology> researched = EnumSet.noneOf(Technology.class);

    public TechTree() {
        
        researched.add(Technology.BASIC_COMBAT);
    }

    public boolean isResearched(Technology tech) {
        return researched.contains(tech);
    }

    public boolean canResearch(Technology tech) {
        if (isResearched(tech)) return false;
        Technology prereq = tech.getPrerequisite();
        return prereq == null || isResearched(prereq);
    }

    public boolean research(Technology tech, ResourceManager resources) {
        if (!canResearch(tech)) return false;
        if (tech.getCost() > 0 && !resources.spendGradePoints(tech.getCost())) return false;
        researched.add(tech);
        return true;
    }

    public Set<Technology> getResearched() {
        return EnumSet.copyOf(researched);
    }

    public List<Technology> getAllTechnologies() {
        return Arrays.asList(Technology.values());
    }

    public double getDamageBonusPct() {
        double bonus = 0.0;
        if (isResearched(Technology.BASIC_COMBAT))    bonus += 0.05;
        if (isResearched(Technology.ADVANCED_COMBAT)) bonus += 0.10;
        if (isResearched(Technology.MASTER_COMBAT))   bonus += 0.15;
        return bonus;
    }

    public double getCECostMultiplier() {
        double mult = 1.0;
        if (isResearched(Technology.CE_EFFICIENCY)) mult -= 0.15;
        if (isResearched(Technology.CE_MASTERY))    mult -= 0.05;
        return Math.max(0.5, mult);
    }

    public int getBonusMaxCE() {
        return isResearched(Technology.CE_MASTERY) ? 30 : 0;
    }

    public double getSpawnChanceMultiplier() {
        double mult = 1.0;
        if (isResearched(Technology.BARRIER_BASICS))    mult -= 0.20;
        if (isResearched(Technology.ADVANCED_BARRIERS)) mult -= 0.40;
        return Math.max(0.0, mult);
    }

    public double getFieldHealBonus() {
        return isResearched(Technology.FIELD_MEDICINE) ? 0.10 : 0.0;
    }
}
