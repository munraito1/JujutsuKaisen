package models;

import enums.Grade;
import techniques.CursedTechnique;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for unique named sorcerer characters.
 * Each named sorcerer has unique techniques and a special ability.
 */
public abstract class NamedSorcerer extends Sorcerer {

    private final List<CursedTechnique> techniques;
    private String title;

    public NamedSorcerer(String name, String title, int maxHp, int attack, int defense,
                         int speed, int maxCursedEnergy, Grade grade) {
        super(name, maxHp, attack, defense, speed, maxCursedEnergy, grade);
        this.title = title;
        this.techniques = new ArrayList<>();
    }

    public void addTechnique(CursedTechnique technique) {
        techniques.add(technique);
    }

    public List<CursedTechnique> getTechniques() {
        return new ArrayList<>(techniques);
    }

    public String getTitle() { return title; }
    protected void setTitle(String title) { this.title = title; }

    /**
     * Use this sorcerer's signature ability on a target.
     */
    public abstract void useSpecialAbility(Combatant target);

    /** Cursed energy cost of the signature ability. */
    public abstract int getAbilityCost();

    /** Display name of the signature ability. */
    public abstract String getAbilityName();

    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s ===%n", getName()));
        sb.append(String.format("Title: %s%n", title));
        sb.append(String.format("Grade: %s%n", getGrade().getDisplayName()));
        sb.append(String.format("Level: %d%n", getLevel()));
        sb.append(String.format("HP: %d/%d%n", getHp(), getMaxHp()));
        sb.append(String.format("Cursed Energy: %d/%d%n", getCursedEnergy(), getMaxCursedEnergy()));
        sb.append(String.format("Attack: %d | Defense: %d | Speed: %d%n",
                getAttack(), getDefense(), getSpeed()));
        if (!techniques.isEmpty()) {
            sb.append("Techniques:\n");
            for (CursedTechnique tech : techniques) {
                sb.append(String.format("  - %s (CE cost: %d)%n",
                        tech.getName(), tech.getCursedEnergyCost()));
            }
        }
        return sb.toString();
    }
}
