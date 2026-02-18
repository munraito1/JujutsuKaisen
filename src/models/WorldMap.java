package models;

import enums.DistrictStatus;
import models.curses.Jogo;
import models.curses.Mahito;

import java.util.ArrayList;
import java.util.List;

/**
 * World map as a node graph of Districts.
 * Each district is a node connected to neighbors via edges.
 */
public class WorldMap {

    private final List<District> districts;
    private District startingDistrict;

    public WorldMap() {
        this.districts = new ArrayList<>();
    }

    /**
     * Builds the default Tokyo map with 8 districts.
     *
     * Graph layout:
     *          Akihabara
     *              |
     * Ikebukuro - Shinjuku - Asakusa
     *              |
     *   Harajuku - JujutsuHigh - Shibuya
     *              |
     *           Roppongi
     */
    public void buildDefaultMap() {
        districts.clear();

        // Create districts (x,y are screen coordinates for rendering)
        District jujutsuHigh = new District("Jujutsu High", "Home base. Sorcerers heal between turns here.",
                DistrictStatus.CONTROLLED, 400, 300, 0, 0);

        District shibuya = new District("Shibuya", "Dense urban district crawling with powerful curses.",
                DistrictStatus.HOSTILE, 600, 300, 3, 15);

        District shinjuku = new District("Shinjuku", "Government district under curse threat.",
                DistrictStatus.HOSTILE, 400, 150, 2, 10);

        District roppongi = new District("Roppongi", "Entertainment district with low-level curse activity.",
                DistrictStatus.HOSTILE, 400, 450, 1, 8);

        District ikebukuro = new District("Ikebukuro", "Commercial hub. Locked until Shinjuku is secured.",
                DistrictStatus.LOCKED, 200, 150, 3, 12);

        District akihabara = new District("Akihabara", "Tech district. Locked until nearby areas are cleared.",
                DistrictStatus.LOCKED, 400, 30, 4, 18);

        District harajuku = new District("Harajuku", "Fashion district with minor curse presence.",
                DistrictStatus.HOSTILE, 200, 300, 1, 6);

        District asakusa = new District("Asakusa", "Ancient temple district with very powerful curses.",
                DistrictStatus.HOSTILE, 600, 150, 4, 20);

        // Build graph edges
        jujutsuHigh.addNeighbor(shibuya);
        jujutsuHigh.addNeighbor(shinjuku);
        jujutsuHigh.addNeighbor(roppongi);
        jujutsuHigh.addNeighbor(harajuku);

        shinjuku.addNeighbor(ikebukuro);
        shinjuku.addNeighbor(asakusa);
        shinjuku.addNeighbor(akihabara);

        ikebukuro.addNeighbor(akihabara);

        // Populate enemies
        populateEnemies(shibuya, shinjuku, roppongi, harajuku, asakusa);

        // Add all to list
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
        // Shibuya: CursedSpiritMob swarm + Mahito (curseLevel=3)
        SorcererTeam shibuyaEnemies = new SorcererTeam("Shibuya Curses");
        shibuyaEnemies.addMember(CursedSpiritMob.createSwarm());
        shibuyaEnemies.addMember(new Mahito());
        shibuya.addEnemyTeam(shibuyaEnemies);

        // Shinjuku: Grade 2 pack x2 (curseLevel=2)
        SorcererTeam shinjukuEnemies = new SorcererTeam("Shinjuku Curses");
        shinjukuEnemies.addMember(CursedSpiritMob.createGrade2Pack());
        shinjukuEnemies.addMember(CursedSpiritMob.createGrade2Pack());
        shinjuku.addEnemyTeam(shinjukuEnemies);

        // Roppongi: Swarm x2 (curseLevel=1, easy start)
        SorcererTeam roppongiEnemies = new SorcererTeam("Roppongi Curses");
        roppongiEnemies.addMember(CursedSpiritMob.createSwarm());
        roppongiEnemies.addMember(CursedSpiritMob.createSwarm());
        roppongi.addEnemyTeam(roppongiEnemies);

        // Harajuku: Swarm (curseLevel=1, easy start)
        SorcererTeam harajukuEnemies = new SorcererTeam("Harajuku Curses");
        harajukuEnemies.addMember(CursedSpiritMob.createSwarm());
        harajuku.addEnemyTeam(harajukuEnemies);

        // Asakusa: Jogo + Grade 2 pack (curseLevel=4)
        SorcererTeam asakusaEnemies = new SorcererTeam("Asakusa Curses");
        asakusaEnemies.addMember(new Jogo());
        asakusaEnemies.addMember(CursedSpiritMob.createGrade2Pack());
        asakusa.addEnemyTeam(asakusaEnemies);
    }

    /**
     * Unlock districts whose prerequisites are met.
     * - Ikebukuro unlocks when Shinjuku is CONTROLLED.
     * - Akihabara unlocks when Ikebukuro or Shinjuku is CONTROLLED.
     */
    public void updateLockedDistricts() {
        for (District d : districts) {
            if (d.getStatus() != DistrictStatus.LOCKED) continue;

            if ("Ikebukuro".equals(d.getName())) {
                if (isControlled("Shinjuku")) {
                    d.setStatus(DistrictStatus.HOSTILE);
                    // Spawn enemies for newly unlocked district
                    SorcererTeam enemies = new SorcererTeam("Ikebukuro Curses");
                    enemies.addMember(CursedSpiritMob.createGrade2Pack());
                    enemies.addMember(CursedSpiritMob.createGrade1Curse());
                    d.addEnemyTeam(enemies);
                    d.setCurseLevel(3);
                }
            } else if ("Akihabara".equals(d.getName())) {
                if (isControlled("Ikebukuro") || isControlled("Shinjuku")) {
                    d.setStatus(DistrictStatus.HOSTILE);
                    SorcererTeam enemies = new SorcererTeam("Akihabara Curses");
                    enemies.addMember(CursedSpiritMob.createGrade1Curse());
                    enemies.addMember(CursedSpiritMob.createGrade2Pack());
                    enemies.addMember(CursedSpiritMob.createGrade2Pack());
                    d.addEnemyTeam(enemies);
                    d.setCurseLevel(4);
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
