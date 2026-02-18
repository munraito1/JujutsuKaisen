package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A team of combatants (max 5 members).
 * Used for both sorcerer squads and curse groups.
 */
public class SorcererTeam {

    private static final int MAX_TEAM_SIZE = 5;

    private String teamName;
    private final List<Combatant> members;

    public SorcererTeam(String teamName) {
        this.teamName = teamName;
        this.members = new ArrayList<>();
    }

    public boolean addMember(Combatant combatant) {
        if (members.size() >= MAX_TEAM_SIZE) {
            System.out.printf("Team %s is full! (max %d members)%n", teamName, MAX_TEAM_SIZE);
            return false;
        }
        members.add(combatant);
        return true;
    }

    public boolean removeMember(Combatant combatant) {
        return members.remove(combatant);
    }

    public List<Combatant> getMembers() {
        return new ArrayList<>(members);
    }

    public List<Combatant> getAliveMembers() {
        return members.stream()
                .filter(Combatant::isAlive)
                .collect(Collectors.toList());
    }

    public boolean isDefeated() {
        return getAliveMembers().isEmpty();
    }

    public int getSize() { return members.size(); }
    public String getTeamName() { return teamName; }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Team: %s (%d/%d alive) ===%n",
                teamName, getAliveMembers().size(), members.size()));
        for (Combatant member : members) {
            String status = member.isAlive() ? "ALIVE" : "DEAD";
            sb.append(String.format("  [%s] %s%n", status, member));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Team %s [%d/%d alive]",
                teamName, getAliveMembers().size(), members.size());
    }
}
