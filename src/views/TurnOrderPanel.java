package views;

import controllers.BattleManager;
import models.Combatant;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TurnOrderPanel extends JPanel {

    private static final Color BG_COLOR    = new Color(42, 40, 37);
    private static final Color CURRENT_BG  = new Color(220, 185, 30);
    private static final Color CURRENT_FG  = new Color(20, 18, 14);
    private static final Color PLAYER_BG   = new Color(50, 90, 160);
    private static final Color PLAYER_FG   = new Color(200, 220, 255);
    private static final Color ENEMY_BG    = new Color(160, 45, 45);
    private static final Color ENEMY_FG    = new Color(255, 200, 200);

    public TurnOrderPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
    }

    public void updateTurnOrder(List<Combatant> turnOrder, Combatant current, BattleManager manager) {
        removeAll();

        JLabel header = new JLabel("Очередь: ");
        header.setFont(new Font("Arial", Font.BOLD, 11));
        header.setForeground(new Color(160, 158, 150));
        add(header);

        for (Combatant unit : turnOrder) {
            if (!unit.isAlive()) continue;

            boolean isCurrent = (unit == current);
            boolean isPlayer  = manager.isPlayerUnit(unit);

            String shortName = getShortName(unit.getName());
            JLabel label = new JLabel(shortName);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            isCurrent ? new Color(255, 215, 0) : new Color(60, 58, 55), 1),
                    BorderFactory.createEmptyBorder(2, 7, 2, 7)));
            label.setFont(new Font("Arial", isCurrent ? Font.BOLD : Font.PLAIN, 11));

            if (isCurrent) {
                label.setBackground(CURRENT_BG);
                label.setForeground(CURRENT_FG);
            } else if (isPlayer) {
                label.setBackground(PLAYER_BG);
                label.setForeground(PLAYER_FG);
            } else {
                label.setBackground(ENEMY_BG);
                label.setForeground(ENEMY_FG);
            }

            label.setToolTipText(unit.getName() + " (Скор.: " + unit.getSpeed() + ")");
            add(label);
        }

        revalidate();
        repaint();
    }

    private String getShortName(String name) {
        String first = name.split(" ")[0];
        return first.substring(0, Math.min(4, first.length()));
    }
}
