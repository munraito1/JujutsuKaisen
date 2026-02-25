package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameActionPanel extends JPanel {

    private static final Color BG_COLOR        = new Color(42, 40, 37);
    private static final Color TRAVEL_COLOR    = new Color(50, 110, 200);
    private static final Color MISSION_COLOR   = new Color(190, 55, 55);
    private static final Color END_TURN_COLOR  = new Color(110, 110, 110);
    private static final Color TEAM_INFO_COLOR = new Color(65, 150, 70);
    private static final Color BUILDINGS_COLOR = new Color(145, 90, 25);
    private static final Color TECH_COLOR      = new Color(90, 55, 170);

    private JButton travelButton;
    private JButton missionButton;
    private JButton endTurnButton;
    private JButton teamInfoButton;
    private JButton buildingsButton;
    private JButton techTreeButton;

    private GameActionListener actionListener;

    public interface GameActionListener {
        void onTravelClicked();
        void onStartMissionClicked();
        void onEndTurnClicked();
        void onTeamInfoClicked();
        void onBuildingsClicked();
        void onTechTreeClicked();
    }

    public GameActionPanel() {
        setBackground(BG_COLOR);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 6));
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        travelButton    = createButton("▶ Перемещение",   TRAVEL_COLOR);
        missionButton   = createButton("⚔ Начать миссию", MISSION_COLOR);
        buildingsButton = createButton("🏛 Здания",        BUILDINGS_COLOR);
        techTreeButton  = createButton("🔬 Технологии",    TECH_COLOR);
        endTurnButton   = createButton("⏭ Конец хода",    END_TURN_COLOR);
        teamInfoButton  = createButton("👥 Команда",       TEAM_INFO_COLOR);

        add(travelButton);
        add(missionButton);
        add(buildingsButton);
        add(techTreeButton);
        add(endTurnButton);
        add(teamInfoButton);

        travelButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onTravelClicked();
        });
        missionButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onStartMissionClicked();
        });
        buildingsButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onBuildingsClicked();
        });
        techTreeButton.addActionListener(e -> {
            if (actionListener != null) actionListener.onTechTreeClicked();
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

    public void setTravelEnabled(boolean enabled)  { travelButton.setEnabled(enabled); }
    public void setMissionEnabled(boolean enabled) { missionButton.setEnabled(enabled); }
    public void setEndTurnEnabled(boolean enabled) { endTurnButton.setEnabled(enabled); }

    private JButton createButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(bg.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }
}
