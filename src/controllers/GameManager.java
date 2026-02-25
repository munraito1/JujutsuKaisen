package controllers;

import enums.BattleState;
import enums.DistrictStatus;
import enums.GamePhase;
import models.*;
import models.TechTree;
import models.Mission;
import models.GameEvent;
import models.NamedSorcerer;
import models.heroes.InumakiToge;
import models.heroes.MakiZenin;
import models.heroes.Panda;
import models.heroes.SatoruGojo;
import systems.EventSystem;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private static final int    STARTING_YUAN    = 300;
    private static final double BASE_HEAL_PCT    = 0.30;
    private static final double BASE_SPAWN_CHANCE = 0.10;

    private static final int YUAN_PER_CURSE_LEVEL = 40;
    
    private static final int GP_PER_CURSE_LEVEL   = 10;

    private static final int XP_PER_CURSE_LEVEL = 50;

    private final WorldMap        worldMap;
    private final SorcererTeam    playerTeam;
    private final BuildingManager buildingManager;
    private final ResourceManager resourceManager;
    private final TechTree        techTree;
    private final EventSystem     eventSystem;
    private Mission               currentMission;

    private final List<NamedSorcerer> heroPool;        
    private final List<NamedSorcerer> heroRoster;      
    private final List<Integer>       heroCosts;        

    private District  currentDistrict;
    private int       turnNumber;
    private GamePhase phase;
    private final List<GameListener> listeners;

    public GameManager(SorcererTeam playerTeam) {
        this.playerTeam      = playerTeam;
        this.worldMap        = new WorldMap();
        this.buildingManager = new BuildingManager();
        this.resourceManager = new ResourceManager(STARTING_YUAN);
        this.techTree        = new TechTree();
        this.eventSystem     = new EventSystem();
        this.heroPool        = new ArrayList<>();
        this.heroRoster      = new ArrayList<>();
        this.heroCosts       = new ArrayList<>();
        this.listeners       = new ArrayList<>();
        initHeroRoster();
        this.turnNumber      = 1;
        this.phase           = GamePhase.MAP_OVERVIEW;

        worldMap.buildDefaultMap();
        currentDistrict = worldMap.getStartingDistrict();
    }

    private void initHeroRoster() {
        heroRoster.add(new MakiZenin());   heroCosts.add(5);
        heroRoster.add(new InumakiToge()); heroCosts.add(8);
        heroRoster.add(new Panda());       heroCosts.add(8);
        heroRoster.add(new SatoruGojo());  heroCosts.add(20);
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public int     getYuan()              { return resourceManager.getYuan(); }
    public void    addYuan(int amount)    { resourceManager.addYuan(amount); }
    public boolean spendYuan(int amount)  { return resourceManager.spendYuan(amount); }

    public int     getGradePoints()           { return resourceManager.getGradePoints(); }
    public void    addGradePoints(int amount) { resourceManager.addGradePoints(amount); }

    public ResourceManager getResourceManager() { return resourceManager; }

    public boolean moveToDistrict(District target) {
        if (target == null) return false;
        if (target.getStatus() == DistrictStatus.LOCKED) {
            fireMessage("Нельзя перейти в «" + target.getName() + "» — район заблокирован!");
            return false;
        }
        if (!currentDistrict.isNeighbor(target)) {
            fireMessage("Нельзя перейти в «" + target.getName() + "» — район не граничит с текущим!");
            return false;
        }

        currentDistrict = target;
        phase = GamePhase.MAP_OVERVIEW;
        fireDistrictChanged(currentDistrict);
        fireMessage("Прибыли в «" + target.getName() + "».");
        return true;
    }

    public SorcererTeam startMission() {
        if (!currentDistrict.hasEnemies()) {
            fireMessage("В районе «" + currentDistrict.getName() + "» нет врагов.");
            return null;
        }

        currentMission = currentDistrict.getMission();
        if (currentMission == null) {
            currentMission = Mission.extermination();
            currentDistrict.setMission(currentMission);
        }

        phase = GamePhase.IN_BATTLE;
        SorcererTeam enemies = currentDistrict.getCombinedEnemyTeam();
        fireBattleStarted(currentDistrict);
        return enemies;
    }

    public Mission getCurrentMission() { return currentMission; }

    public void onBattleFinished(BattleState result) {
        phase = GamePhase.MAP_OVERVIEW;

        if (result == BattleState.VICTORY) {
            int curseLevel = Math.max(currentDistrict.getCurseLevel(), 1);
            double mult    = (currentMission != null) ? currentMission.getRewardMultiplier() : 1.0;
            int yuanReward = (int)(curseLevel * YUAN_PER_CURSE_LEVEL * mult);
            int gpReward   = (int)(curseLevel * GP_PER_CURSE_LEVEL   * mult);
            int xpReward   = (int)(curseLevel * XP_PER_CURSE_LEVEL   * mult);

            resourceManager.addYuan(yuanReward);
            resourceManager.addGradePoints(gpReward);

            int totalLevels = 0;
            for (Combatant c : playerTeam.getAliveMembers()) {
                totalLevels += c.addExperience(xpReward);
            }
            String levelMsg = totalLevels > 0
                    ? String.format(" (%d повышени%s уровня!)", totalLevels,
                        totalLevels == 1 ? "е" : "я")
                    : "";
            fireMessage(String.format("Команда получает %d ОП опыта.%s", xpReward, levelMsg));

            currentDistrict.clearEnemies();
            currentDistrict.setMission(null);
            currentDistrict.setStatus(DistrictStatus.CONTROLLED);
            worldMap.updateLockedDistricts();

            String missionName = (currentMission != null) ? currentMission.getDisplayName() : "Уничтожение";
            fireMessage(String.format("[%s] «%s» под контролем! Награда: +%d¥, +%d ОР.",
                    missionName, currentDistrict.getName(), yuanReward, gpReward));
        } else {
            fireMessage("Миссия провалена! Отступаем на базу...");
            currentDistrict = worldMap.getStartingDistrict();
        }

        fireBattleFinished(currentDistrict, result);
        fireDistrictChanged(currentDistrict);
    }

    public void endTurn() {
        turnNumber++;
        District base  = worldMap.getStartingDistrict();
        boolean atBase = currentDistrict == base;

        int districtYuan = 0;
        for (District d : worldMap.getDistricts()) {
            if (d.getStatus() == DistrictStatus.CONTROLLED) {
                districtYuan += d.getIncomePerTurn();
            }
        }
        int buildingYuan = buildingManager.getTotalYuanIncome();
        resourceManager.addYuan(districtYuan + buildingYuan);

        if (districtYuan + buildingYuan > 0) {
            fireMessage(String.format("Доход: +%d¥ с районов, +%d¥ со зданий. Итого: ¥%d",
                    districtYuan, buildingYuan, resourceManager.getYuan()));
        }

        double healPct = atBase
                ? BASE_HEAL_PCT + buildingManager.getTotalHealBonus(true)
                : buildingManager.getTotalHealBonus(false) + techTree.getFieldHealBonus();
        if (healPct > 0) {
            for (Combatant c : playerTeam.getAliveMembers()) {
                c.heal((int) (c.getMaxHp() * healPct));
            }
            String location = atBase ? "на базе" : "в поле";
            fireMessage(String.format("Команда восстановила %.0f%% HP %s.",
                    healPct * 100, location));
        }

        double spawnChance = BASE_SPAWN_CHANCE
                * buildingManager.getSpawnChanceMultiplier()
                * techTree.getSpawnChanceMultiplier();
        for (District d : worldMap.getDistricts()) {
            if (d.getStatus() == DistrictStatus.CONTROLLED && d != base) {
                if (Math.random() < spawnChance) {
                    d.setStatus(DistrictStatus.CONTESTED);
                    SorcererTeam newEnemies = new SorcererTeam("Новые проклятия");
                    newEnemies.addMember(CursedSpiritMob.createSwarm());
                    d.addEnemyTeam(newEnemies);
                    d.setCurseLevel(1);
                    d.setMission(Mission.defense());
                    fireMessage("Обнаружена активность проклятий в «" + d.getName() + "»! [Оборона]");
                }
            }
        }

        List<District> controlled = new ArrayList<>();
        for (District d : worldMap.getDistricts()) {
            if (d.getStatus() == DistrictStatus.CONTROLLED && d != base) controlled.add(d);
        }
        GameEvent event = eventSystem.rollEvent(
                worldMap.getDistricts(), controlled, playerTeam, turnNumber);
        if (event != null) {
            fireMessage(event.getMessage());
        }

        fireTurnAdvanced(turnNumber);
    }

    public List<NamedSorcerer> getAvailableForRecruitment() {
        int officeLevel = buildingManager.getRecruitmentOfficeLevel();
        List<NamedSorcerer> result = new ArrayList<>();
        for (int i = 0; i < heroRoster.size(); i++) {
            NamedSorcerer hero = heroRoster.get(i);
            boolean alreadyRecruited = heroPool.contains(hero)
                    || playerTeam.getMembers().contains(hero);
            int reqLevel = (i == 0) ? 1 : (i <= 2) ? 2 : 3;
            if (officeLevel >= reqLevel && !alreadyRecruited) {
                result.add(hero);
            }
        }
        return result;
    }

    public int getRecruitCost(NamedSorcerer hero) {
        int idx = heroRoster.indexOf(hero);
        return idx >= 0 ? heroCosts.get(idx) : 999;
    }

    public boolean recruitHero(NamedSorcerer hero) {
        int cost = getRecruitCost(hero);
        if (!resourceManager.spendGradePoints(cost)) {
            fireMessage("Недостаточно ОР для найма «" + hero.getName() + "».");
            return false;
        }
        heroPool.add(hero);
        fireMessage("«" + hero.getName() + "» завербован! Добавьте его в команду.");
        return true;
    }

    public boolean addHeroToTeam(NamedSorcerer hero) {
        if (!heroPool.contains(hero)) return false;
        if (!playerTeam.addMember(hero)) {
            fireMessage("Команда уже полная (5 слотов).");
            return false;
        }
        heroPool.remove(hero);
        fireMessage("«" + hero.getName() + "» присоединился к команде!");
        return true;
    }

    public boolean removeHeroFromTeam(NamedSorcerer hero) {
        if (!playerTeam.getMembers().contains(hero)) return false;
        if (playerTeam.getSize() <= 1) {
            fireMessage("В команде должен быть хотя бы один боец.");
            return false;
        }
        playerTeam.removeMember(hero);
        heroPool.add(hero);
        fireMessage("«" + hero.getName() + "» выведен из команды.");
        return true;
    }

    public List<NamedSorcerer> getHeroPool() { return heroPool; }

    public WorldMap        getWorldMap()         { return worldMap; }
    public SorcererTeam    getPlayerTeam()       { return playerTeam; }
    public BuildingManager getBuildingManager()  { return buildingManager; }
    public TechTree        getTechTree()         { return techTree; }
    public District        getCurrentDistrict()  { return currentDistrict; }
    public int             getTurnNumber()       { return turnNumber; }
    public GamePhase       getPhase()            { return phase; }

    private void fireDistrictChanged(District d) {
        for (GameListener l : listeners) l.onDistrictChanged(d);
    }

    private void fireTurnAdvanced(int turn) {
        for (GameListener l : listeners) l.onTurnAdvanced(turn);
    }

    private void fireBattleStarted(District d) {
        for (GameListener l : listeners) l.onBattleStarted(d);
    }

    private void fireBattleFinished(District d, BattleState result) {
        for (GameListener l : listeners) l.onBattleFinished(d, result);
    }

    private void fireMessage(String msg) {
        for (GameListener l : listeners) l.onMessage(msg);
    }
}
