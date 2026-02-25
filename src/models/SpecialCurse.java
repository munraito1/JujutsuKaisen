package models;

import enums.Grade;
import techniques.CursedTechnique;

import java.util.ArrayList;
import java.util.List;

public abstract class SpecialCurse extends CursedSpirit {

    private final List<CursedTechnique> techniques;
    private String domainName;

    public SpecialCurse(String name, int maxHp, int attack, int defense, int speed,
                        int maxCursedEnergy, String domainName) {
        super(name, maxHp, attack, defense, speed, maxCursedEnergy, Grade.SPECIAL_GRADE);
        this.techniques = new ArrayList<>();
        this.domainName = domainName;
    }

    public void addTechnique(CursedTechnique technique) {
        techniques.add(technique);
    }

    public List<CursedTechnique> getTechniques() {
        return new ArrayList<>(techniques);
    }

    public String getDomainName() { return domainName; }

    public abstract void useSpecialAbility(Combatant target);

    public abstract int getAbilityCost();

    public abstract String getAbilityName();

    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s ===%n", getName()));
        sb.append(String.format("Grade: %s%n", getCurseGrade().getDisplayName()));
        sb.append(String.format("Domain: %s%n", domainName));
        sb.append(String.format("HP: %d/%d%n", getHp(), getMaxHp()));
        sb.append(String.format("Cursed Energy: %d/%d%n", getCursedEnergy(), getMaxCursedEnergy()));
        sb.append(String.format("Attack: %d | Defense: %d | Speed: %d%n",
                getAttack(), getDefense(), getSpeed()));
        return sb.toString();
    }
}
