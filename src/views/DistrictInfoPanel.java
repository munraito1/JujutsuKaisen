package views;

import enums.DistrictStatus;
import models.Combatant;
import models.District;
import models.SorcererTeam;

import javax.swing.*;
import java.awt.*;

/**
 * Side panel showing information about the selected district.
 */
public class DistrictInfoPanel extends JPanel {

    private static final Color BG_COLOR = new Color(60, 58, 55);
    private static final Color TEXT_COLOR = new Color(220, 220, 210);
    private static final Color LABEL_COLOR = new Color(160, 160, 150);
    private static final Color CONTROLLED_COLOR = new Color(80, 180, 80);
    private static final Color HOSTILE_COLOR = new Color(200, 60, 60);
    private static final Color LOCKED_COLOR = new Color(120, 120, 120);
    private static final Color CONTESTED_COLOR = new Color(220, 200, 50);

    private JLabel nameLabel;
    private JLabel statusLabel;
    private JLabel threatLabel;
    private JLabel incomeLabel;
    private JTextArea descriptionArea;
    private JTextArea enemyArea;

    public DistrictInfoPanel() {
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(210, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("District Info");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(LEFT_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(10));

        // District name
        nameLabel = createLabel("--", new Font("Arial", Font.BOLD, 16), TEXT_COLOR);
        add(nameLabel);
        add(Box.createVerticalStrut(6));

        // Status
        statusLabel = createLabel("Status: --", new Font("Arial", Font.PLAIN, 12), LABEL_COLOR);
        add(statusLabel);
        add(Box.createVerticalStrut(4));

        // Threat level
        threatLabel = createLabel("Threat: --", new Font("Arial", Font.PLAIN, 12), LABEL_COLOR);
        add(threatLabel);
        add(Box.createVerticalStrut(4));

        // Income
        incomeLabel = createLabel("Income: --", new Font("Arial", Font.PLAIN, 12), LABEL_COLOR);
        add(incomeLabel);
        add(Box.createVerticalStrut(10));

        // Description
        JLabel descTitle = createLabel("Description:", new Font("Arial", Font.BOLD, 11), LABEL_COLOR);
        add(descTitle);
        add(Box.createVerticalStrut(4));

        descriptionArea = new JTextArea(3, 18);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 11));
        descriptionArea.setBackground(new Color(50, 48, 45));
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        descriptionArea.setAlignmentX(LEFT_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(190, 60));
        add(descriptionArea);
        add(Box.createVerticalStrut(10));

        // Enemy info
        JLabel enemyTitle = createLabel("Enemies:", new Font("Arial", Font.BOLD, 11), LABEL_COLOR);
        add(enemyTitle);
        add(Box.createVerticalStrut(4));

        enemyArea = new JTextArea(6, 18);
        enemyArea.setEditable(false);
        enemyArea.setLineWrap(true);
        enemyArea.setWrapStyleWord(true);
        enemyArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        enemyArea.setBackground(new Color(50, 48, 45));
        enemyArea.setForeground(TEXT_COLOR);
        enemyArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        enemyArea.setAlignmentX(LEFT_ALIGNMENT);
        enemyArea.setMaximumSize(new Dimension(190, 120));
        add(enemyArea);

        add(Box.createVerticalGlue());

        clear();
    }

    public void showDistrict(District district) {
        if (district == null) {
            clear();
            return;
        }

        nameLabel.setText(district.getName());

        DistrictStatus status = district.getStatus();
        statusLabel.setText("Status: " + status.getDisplayName());
        statusLabel.setForeground(getStatusColor(status));

        int threat = district.getCurseLevel();
        String stars = "";
        for (int i = 0; i < threat; i++) stars += "*";
        for (int i = threat; i < 5; i++) stars += "-";
        threatLabel.setText("Threat: [" + stars + "] " + threat + "/5");
        threatLabel.setForeground(threat >= 3 ? HOSTILE_COLOR : LABEL_COLOR);

        incomeLabel.setText("Income: " + district.getIncomePerTurn() + " CE/turn");

        descriptionArea.setText(district.getDescription());

        // Enemy list
        if (district.hasEnemies()) {
            StringBuilder sb = new StringBuilder();
            for (SorcererTeam team : district.getEnemyTeams()) {
                for (Combatant c : team.getMembers()) {
                    sb.append("- ").append(c.getName());
                    sb.append(" (HP:").append(c.getHp()).append(")");
                    sb.append("\n");
                }
            }
            enemyArea.setText(sb.toString().trim());
        } else {
            enemyArea.setText(status == DistrictStatus.LOCKED ? "Unknown" : "None");
        }
    }

    public void clear() {
        nameLabel.setText("--");
        statusLabel.setText("Status: --");
        statusLabel.setForeground(LABEL_COLOR);
        threatLabel.setText("Threat: --");
        threatLabel.setForeground(LABEL_COLOR);
        incomeLabel.setText("Income: --");
        descriptionArea.setText("");
        enemyArea.setText("");
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private Color getStatusColor(DistrictStatus status) {
        switch (status) {
            case CONTROLLED: return CONTROLLED_COLOR;
            case HOSTILE: return HOSTILE_COLOR;
            case LOCKED: return LOCKED_COLOR;
            case CONTESTED: return CONTESTED_COLOR;
            default: return LABEL_COLOR;
        }
    }
}
