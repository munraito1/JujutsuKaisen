package models.buildings;

import models.Building;
import models.Combatant;
import models.SorcererTeam;

public class Dojo extends Building {

    public Dojo() {
        super("Додзё",
                "Тренировочный зал. Постоянно улучшает характеристики героев — "
                + "каждая постройка и улучшение даёт всем героям новый уровень.",
                40, 3);
    }

    @Override
    public void onBuilt(SorcererTeam playerTeam) {
        levelUpAll(playerTeam);
    }

    @Override
    public void onUpgraded(SorcererTeam playerTeam) {
        levelUpAll(playerTeam);
    }

    private void levelUpAll(SorcererTeam team) {
        for (Combatant c : team.getMembers()) {
            c.levelUp();
        }
    }

    @Override
    public String getEffectDescription() {
        if (!isBuilt()) return "Не построено.";
        return "Герои получили уровень " + getLevel() + " раз(а). Улучшение даст ещё один.";
    }
}
