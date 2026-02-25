package models;

import enums.DistrictStatus;
import models.curses.Choso;
import models.curses.Jogo;
import models.curses.Mahito;
import models.curses.Sukuna;
import models.Mission;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {

    private final List<District> districts;
    private District startingDistrict;

    public WorldMap() {
        this.districts = new ArrayList<>();
    }

    public void buildDefaultMap() {
        districts.clear();

        District jujutsuHigh = new District("Jujutsu High",
                "Главная база магов. Команда восстанавливает HP между ходами.",
                DistrictStatus.CONTROLLED, 400, 300, 0, 0);

        District shibuya = new District("Сибуя",
                "Плотный городской район, кишащий мощными проклятиями.",
                DistrictStatus.HOSTILE, 600, 300, 3, 15);

        District shinjuku = new District("Синдзюку",
                "Правительственный район под угрозой проклятий.",
                DistrictStatus.HOSTILE, 400, 150, 2, 10);

        District roppongi = new District("Роппонги",
                "Развлекательный район с малой активностью проклятий.",
                DistrictStatus.HOSTILE, 400, 450, 1, 8);

        District ikebukuro = new District("Икебукуро",
                "Торговый центр. Откроется после захвата Синдзюку.",
                DistrictStatus.LOCKED, 200, 150, 3, 12);

        District akihabara = new District("Акихабара",
                "Технологический район. Откроется после очистки соседних.",
                DistrictStatus.LOCKED, 400, 30, 4, 18);

        District harajuku = new District("Харадзюку",
                "Модный район с небольшим присутствием проклятий.",
                DistrictStatus.HOSTILE, 200, 300, 1, 6);

        District asakusa = new District("Асакуса",
                "Древний храмовый район с очень мощными проклятиями.",
                DistrictStatus.HOSTILE, 600, 150, 4, 20);

        jujutsuHigh.addNeighbor(shibuya);
        jujutsuHigh.addNeighbor(shinjuku);
        jujutsuHigh.addNeighbor(roppongi);
        jujutsuHigh.addNeighbor(harajuku);

        shinjuku.addNeighbor(ikebukuro);
        shinjuku.addNeighbor(asakusa);
        shinjuku.addNeighbor(akihabara);

        ikebukuro.addNeighbor(akihabara);

        populateEnemies(shibuya, shinjuku, roppongi, harajuku, asakusa);
        assignMissions(shibuya, shinjuku, roppongi, harajuku, asakusa);

        districts.add(jujutsuHigh);
        districts.add(shibuya);
        districts.add(shinjuku);
        districts.add(roppongi);
        districts.add(ikebukuro);
        districts.add(akihabara);
        districts.add(harajuku);
        districts.add(asakusa);

        startingDistrict = jujutsuHigh;
    }

    private void populateEnemies(District shibuya, District shinjuku,
                                  District roppongi, District harajuku,
                                  District asakusa) {
        SorcererTeam shibuyaEnemies = new SorcererTeam("Проклятия Сибуя");
        shibuyaEnemies.addMember(CursedSpiritMob.createSwarm());
        shibuyaEnemies.addMember(new Mahito());
        shibuya.addEnemyTeam(shibuyaEnemies);

        SorcererTeam shinjukuEnemies = new SorcererTeam("Проклятия Синдзюку");
        shinjukuEnemies.addMember(CursedSpiritMob.createGrade2Pack());
        shinjukuEnemies.addMember(CursedSpiritMob.createGrade2Pack());
        shinjuku.addEnemyTeam(shinjukuEnemies);

        SorcererTeam roppongiEnemies = new SorcererTeam("Проклятия Роппонги");
        roppongiEnemies.addMember(CursedSpiritMob.createSwarm());
        roppongiEnemies.addMember(CursedSpiritMob.createSwarm());
        roppongi.addEnemyTeam(roppongiEnemies);

        SorcererTeam harajukuEnemies = new SorcererTeam("Проклятия Харадзюку");
        harajukuEnemies.addMember(CursedSpiritMob.createSwarm());
        harajuku.addEnemyTeam(harajukuEnemies);

        SorcererTeam asakusaEnemies = new SorcererTeam("Проклятия Асакусы");
        asakusaEnemies.addMember(new Jogo());
        asakusaEnemies.addMember(CursedSpiritMob.createGrade2Pack());
        asakusa.addEnemyTeam(asakusaEnemies);
    }

    private void assignMissions(District shibuya, District shinjuku,
                                 District roppongi, District harajuku,
                                 District asakusa) {
        
        roppongi.setMission(Mission.extermination());
        harajuku.setMission(Mission.extermination());
        shinjuku.setMission(Mission.extermination());

        shibuya.setMission(Mission.rescue(8));

        asakusa.setMission(Mission.defense());
    }

    public void updateLockedDistricts() {
        for (District d : districts) {
            if (d.getStatus() != DistrictStatus.LOCKED) continue;

            if ("Икебукуро".equals(d.getName())) {
                if (isControlled("Синдзюку")) {
                    d.setStatus(DistrictStatus.HOSTILE);
                    SorcererTeam enemies = new SorcererTeam("Проклятия Икебукуро");
                    enemies.addMember(new Choso());
                    enemies.addMember(CursedSpiritMob.createGrade1Curse());
                    d.addEnemyTeam(enemies);
                    d.setCurseLevel(3);
                    d.setMission(Mission.extermination());
                }
            } else if ("Акихабара".equals(d.getName())) {
                if (isControlled("Икебукуро") || isControlled("Синдзюку")) {
                    d.setStatus(DistrictStatus.HOSTILE);
                    SorcererTeam enemies = new SorcererTeam("Проклятия Акихабары");
                    enemies.addMember(new Sukuna());
                    enemies.addMember(CursedSpiritMob.createGrade1Curse());
                    d.addEnemyTeam(enemies);
                    d.setCurseLevel(5);
                    d.setMission(Mission.rescue(10));
                }
            }
        }
    }

    private boolean isControlled(String districtName) {
        return districts.stream()
                .anyMatch(d -> d.getName().equals(districtName)
                        && d.getStatus() == DistrictStatus.CONTROLLED);
    }

    public District getStartingDistrict() { return startingDistrict; }
    public List<District> getDistricts() { return districts; }

    public District findByName(String name) {
        return districts.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
