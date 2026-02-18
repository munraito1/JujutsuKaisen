package views;

import controllers.BattleManager;
import models.Combatant;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Displays the turn order as a row of colored labels.
 */
public class TurnOrderPanel extends JPanel {

    private static final Color CURRENT_BG = new Color(255, 220, 50);
    private static final Color PLAYER_BG  = new Color(180, 210, 255);
    private static final Color ENEMY_BG   = new Color(255, 190, 190);

    public TurnOrderPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 3, 2));
        setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
    }

    public void updateTurnOrder(List<Combatant> turnOrder, Combatant current, BattleManager manager) {
        removeAll();

        JLabel header = new JLabel("Turn: ");
        header.setFont(new Font("Arial", Font.BOLD, 11));
        add(header);

        for (Combatant unit : turnOrder) {
            if (!unit.isAlive()) continue;

            String initials = getInitials(unit.getName());
            JLabel label = new JLabel(initials);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            label.setFont(new Font("Arial", unit == current ? Font.BOLD : Font.PLAIN, 11));

            if (unit == current) {
                label.setBackground(CURRENT_BG);
            } else if (manager.isPlayerUnit(unit)) {
                label.setBackground(PLAYER_BG);
            } else {
                label.setBackground(ENEMY_BG);
            }

            label.setToolTipText(unit.getName() + " (SPD: " + unit.getSpeed() + ")");
            add(label);
        }

        revalidate();
        repaint();
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return "" + parts[0].charAt(0) + parts[1].charAt(0);
        }
        return name.substring(0, Math.min(2, name.length()));
    }
}
