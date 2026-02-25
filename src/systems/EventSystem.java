package systems;

import enums.DistrictStatus;
import enums.GameEventType;
import models.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EventSystem {

    private static final double BASE_EVENT_CHANCE = 0.35;
    private final Random rng = new Random();

    public GameEvent rollEvent(List<District> allDistricts,
                               List<District> controlledDistricts,
                               SorcererTeam playerTeam,
                               int turnNumber) {

        if (rng.nextDouble() > BASE_EVENT_CHANCE) return null;

        List<District> hostile = allDistricts.stream()
                .filter(d -> d.getStatus() == DistrictStatus.HOSTILE)
                .collect(Collectors.toList());

        List<District> contested = allDistricts.stream()
                .filter(d -> d.getStatus() == DistrictStatus.CONTESTED)
                .collect(Collectors.toList());

        int roll = rng.nextInt(100);

        if (roll < 15 && !controlledDistricts.isEmpty()) {
            
            return applyReinforcements(playerTeam, controlledDistricts.get(0));

        } else if (roll < 35 && !hostile.isEmpty()) {
            
            District target = hostile.get(rng.nextInt(hostile.size()));
            return applyCursedOutbreak(target, turnNumber);

        } else if (roll < 55 && !controlledDistricts.isEmpty()) {
            
            District target = controlledDistricts.get(rng.nextInt(controlledDistricts.size()));
            return applyCivilianDanger(target);

        } else if (roll < 75 && !hostile.isEmpty() && turnNumber >= 10) {
            
            District target = hostile.get(rng.nextInt(hostile.size()));
            return applySpecialGradeSighting(target);

        } else if (!controlledDistricts.isEmpty() && turnNumber >= 15) {
            
            District target = controlledDistricts.get(rng.nextInt(controlledDistricts.size()));
            return applyDomainEmergency(target);
        }

        return null;
    }

    private GameEvent applyReinforcements(SorcererTeam team, District base) {
        
        for (Combatant c : team.getAliveMembers()) {
            c.heal((int) (c.getMaxHp() * 0.20));
        }
        String msg = "⚡ " + GameEventType.REINFORCEMENTS.getDisplayName()
                + ": Прибыл отряд поддержки! Вся команда восстановила 20% HP.";
        return new GameEvent(GameEventType.REINFORCEMENTS, msg, base);
    }

    private GameEvent applyCursedOutbreak(District target, int turn) {
        
        SorcererTeam outbreak = new SorcererTeam("Вспышка");
        outbreak.addMember(CursedSpiritMob.createSwarm());
        target.addEnemyTeam(outbreak);
        int newLevel = Math.min(5, target.getCurseLevel() + 1);
        target.setCurseLevel(newLevel);

        String msg = "💀 " + GameEventType.CURSED_OUTBREAK.getDisplayName()
                + " в «" + target.getName() + "»: новые проклятия появились!";
        return new GameEvent(GameEventType.CURSED_OUTBREAK, msg, target);
    }

    private GameEvent applyCivilianDanger(District target) {
        
        Mission current = target.getMission();
        if (current == null || current.getType() != enums.MissionType.RESCUE) {
            
            SorcererTeam threat = new SorcererTeam("Угроза");
            threat.addMember(CursedSpiritMob.createSwarm());
            target.addEnemyTeam(threat);
            target.setStatus(DistrictStatus.CONTESTED);
            target.setCurseLevel(Math.max(1, target.getCurseLevel()));
            target.setMission(Mission.rescue(5));
        }

        String msg = "⚠️ " + GameEventType.CIVILIAN_IN_DANGER.getDisplayName()
                + " в «" + target.getName() + "»! Спасение за 5 ходов!";
        return new GameEvent(GameEventType.CIVILIAN_IN_DANGER, msg, target);
    }

    private GameEvent applySpecialGradeSighting(District target) {
        
        int newLevel = Math.min(5, target.getCurseLevel() + 1);
        target.setCurseLevel(newLevel);

        String msg = "💥 " + GameEventType.SPECIAL_GRADE_SIGHTING.getDisplayName()
                + " в «" + target.getName() + "»! Угроза возросла до " + newLevel + ".";
        return new GameEvent(GameEventType.SPECIAL_GRADE_SIGHTING, msg, target);
    }

    private GameEvent applyDomainEmergency(District target) {
        
        SorcererTeam invaders = new SorcererTeam("Вторжение");
        invaders.addMember(CursedSpiritMob.createGrade2Pack());
        target.addEnemyTeam(invaders);
        target.setStatus(DistrictStatus.CONTESTED);
        target.setCurseLevel(Math.min(5, target.getCurseLevel() + 2));
        target.setMission(Mission.defense());

        String msg = "🔴 " + GameEventType.DOMAIN_EMERGENCY.getDisplayName()
                + " в «" + target.getName() + "»! Требуется немедленная оборона!";
        return new GameEvent(GameEventType.DOMAIN_EMERGENCY, msg, target);
    }
}
