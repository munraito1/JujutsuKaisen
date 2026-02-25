package models;

import enums.DistrictStatus;

import java.util.ArrayList;
import java.util.List;
import models.Mission;

public class District {

    private final String name;
    private final String description;
    private DistrictStatus status;
    private final int x; 
    private final int y;
    private int curseLevel; 
    private final List<SorcererTeam> enemyTeams;
    private final List<District> neighbors;
    private int incomePerTurn;
    private Mission currentMission;

    public District(String name, String description, DistrictStatus status,
                    int x, int y, int curseLevel, int incomePerTurn) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.x = x;
        this.y = y;
        this.curseLevel = curseLevel;
        this.incomePerTurn = incomePerTurn;
        this.enemyTeams = new ArrayList<>();
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(District neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
            neighbor.neighbors.add(this); 
        }
    }

    public void addEnemyTeam(SorcererTeam team) {
        enemyTeams.add(team);
    }

    public void clearEnemies() {
        enemyTeams.clear();
        curseLevel = 0;
    }

    public boolean hasEnemies() {
        return !enemyTeams.isEmpty();
    }

    public SorcererTeam getCombinedEnemyTeam() {
        SorcererTeam combined = new SorcererTeam("Curses of " + name);
        for (SorcererTeam team : enemyTeams) {
            for (Combatant c : team.getMembers()) {
                combined.addMember(c);
            }
        }
        return combined;
    }

    public boolean isNeighbor(District other) {
        return neighbors.contains(other);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public DistrictStatus getStatus() { return status; }
    public void setStatus(DistrictStatus status) { this.status = status; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getCurseLevel() { return curseLevel; }
    public void setCurseLevel(int curseLevel) { this.curseLevel = curseLevel; }
    public List<SorcererTeam> getEnemyTeams() { return enemyTeams; }
    public List<District> getNeighbors() { return neighbors; }
    public int getIncomePerTurn() { return incomePerTurn; }
    public Mission getMission() { return currentMission; }
    public void setMission(Mission mission) { this.currentMission = mission; }

    @Override
    public String toString() {
        return String.format("%s [%s, Curse Lv.%d]", name, status.getDisplayName(), curseLevel);
    }
}
