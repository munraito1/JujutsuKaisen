package views;

import javax.swing.*;
import java.awt.*;

/**
 * Bottom panel with game action buttons: Travel, Start Mission, End Turn, Team Info.
 */
public class GameActionPanel extends JPanel {

    private static final Color BG_COLOR = new Color(60, 58, 55);
    private static final Color TRAVEL_COLOR = new Color(60, 120, 200);
    private static final Color MISSION_COLOR = new Color(200, 60, 60);
    private static final Color END_TURN_COLOR = new Color(120, 120, 120);
    private static final Color TEAM_INFO_COLOR = new Color(80, 160, 80);

    private JButton travelButton;
    private JButton missionButton;
    private JButton endTurnButton;
    private JButton teamInfoButton;

    private GameActionListener actionListener;

    public interface GameActionListener {
        void onTravelClicked();
        void onStartMissionClicked();
        void onEndTurnClicked();
        void onTeamInfoClicked();
    }

    public GameActionPanel() {
        setBackground(BG_COLOR);
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        travelButton = createButton("Travel", TRAVEL_COLOR);
        missionButton = createButton("Start Mission", MISSION_COLOR);
        endTurnButton = createButton("End Turn", END_TURN_COLOR);
        teamInfoButton = createButton("Team Info", TEAM_INFO_COLOR);

        add(travelButton);
        add(missionButton);
        add(endTurnButton);
        add(teamInfoButton);

        travelButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onTravelClicked();
        });
        missionButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onStartMissionClicked();
        });
        endTurnButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onEndTurnClicked();
        });
        teamInfoButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onTeamInfoClicked();
        });
    }

    public void setActionListener(GameActionListener listener) {
        this.actionListener = listener;
    }

    public void setTravelEnabled(boolean enabled) {
        travelButton.setEnabled(enabled);
    }

    public void setMissionEnabled(boolean enabled) {
        missionButton.setEnabled(enabled);
    }

    public void setEndTurnEnabled(boolean enabled) {
        endTurnButton.setEnabled(enabled);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }
}
