package views;

import controllers.BattleListener;
import controllers.GameListener;
import controllers.GameManager;
import enums.BattleState;
import models.Combatant;
import models.District;
import models.SorcererTeam;

import javax.swing.*;
import java.awt.*;

/**
 * Main game window with the world map.
 * Implements GameListener to react to game state changes.
 * Launches BattleFrame for combat and handles the result.
 */
public class GameFrame extends JFrame implements GameListener,
        MapPanel.DistrictSelectionListener, GameActionPanel.GameActionListener {

    private final GameManager gameManager;

    private MapPanel mapPanel;
    private DistrictInfoPanel infoPanel;
    private GameActionPanel actionPanel;
    private JLabel statusLabel;
    private JTextArea logArea;

    private District selectedDistrict;

    public GameFrame(SorcererTeam playerTeam) {
        super("Jujutsu Kaisen - Tokyo Map");

        gameManager = new GameManager(playerTeam);
        gameManager.addListener(this);

        initUI();
        refreshMap();
        log("Welcome to Jujutsu Kaisen! You are at " + gameManager.getCurrentDistrict().getName() + ".");
        log("Click on a neighboring district and press Travel to move.");
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));
        setResizable(false);
        getContentPane().setBackground(new Color(50, 48, 45));

        // --- Top: status bar ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 58, 55));
        topPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        statusLabel = new JLabel("Turn 1  |  Jujutsu High");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        topPanel.add(statusLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Tokyo District Map");
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        titleLabel.setForeground(new Color(180, 180, 170));
        topPanel.add(titleLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- Center: map panel ---
        mapPanel = new MapPanel();
        mapPanel.setSelectionListener(this);
        mapPanel.setWorldMap(gameManager.getWorldMap());
        mapPanel.setCurrentDistrict(gameManager.getCurrentDistrict());
        add(mapPanel, BorderLayout.CENTER);

        // --- East: district info ---
        infoPanel = new DistrictInfoPanel();
        add(infoPanel, BorderLayout.EAST);

        // --- South: actions + log ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(50, 48, 45));

        actionPanel = new GameActionPanel();
        actionPanel.setActionListener(this);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(30, 28, 25));
        logArea.setForeground(new Color(200, 200, 190));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Game Log", 0, 0, null, Color.GRAY));
        bottomPanel.add(logScroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        updateActionButtons();
        pack();
        setLocationRelativeTo(null);
    }

    private void refreshMap() {
        mapPanel.setWorldMap(gameManager.getWorldMap());
        mapPanel.setCurrentDistrict(gameManager.getCurrentDistrict());
        updateStatusLabel();
        updateActionButtons();
    }

    private void updateStatusLabel() {
        statusLabel.setText(String.format("Turn %d  |  %s  |  Team: %d/%d alive",
                gameManager.getTurnNumber(),
                gameManager.getCurrentDistrict().getName(),
                gameManager.getPlayerTeam().getAliveMembers().size(),
                gameManager.getPlayerTeam().getSize()));
    }

    private void updateActionButtons() {
        boolean hasSelection = selectedDistrict != null;
        boolean canTravel = hasSelection
                && gameManager.getCurrentDistrict().isNeighbor(selectedDistrict)
                && selectedDistrict.getStatus() != enums.DistrictStatus.LOCKED;
        boolean canMission = gameManager.getCurrentDistrict().hasEnemies();

        actionPanel.setTravelEnabled(canTravel);
        actionPanel.setMissionEnabled(canMission);
        actionPanel.setEndTurnEnabled(true);
    }

    // ==================== MapPanel.DistrictSelectionListener ====================

    @Override
    public void onDistrictSelected(District district) {
        selectedDistrict = district;
        infoPanel.showDistrict(district);
        updateActionButtons();
    }

    // ==================== GameActionPanel.GameActionListener ====================

    @Override
    public void onTravelClicked() {
        if (selectedDistrict == null) return;
        if (gameManager.moveToDistrict(selectedDistrict)) {
            selectedDistrict = null;
            mapPanel.setSelectedDistrict(null);
            infoPanel.showDistrict(gameManager.getCurrentDistrict());
        }
    }

    @Override
    public void onStartMissionClicked() {
        SorcererTeam enemies = gameManager.startMission();
        if (enemies == null) return;

        log("Entering battle in " + gameManager.getCurrentDistrict().getName() + "...");

        // Hide map, launch battle
        setVisible(false);

        SorcererTeam playerCopy = gameManager.getPlayerTeam();
        BattleFrame battleFrame = new BattleFrame(playerCopy, enemies);

        // Override close operation so it doesn't exit the app
        battleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Listen for battle end via the BattleManager's listener
        battleFrame.setBattleEndCallback(result -> {
            SwingUtilities.invokeLater(() -> {
                battleFrame.dispose();
                gameManager.onBattleFinished(result);
                setVisible(true);
                refreshMap();
            });
        });

        battleFrame.setVisible(true);
    }

    @Override
    public void onEndTurnClicked() {
        gameManager.endTurn();
        refreshMap();
    }

    @Override
    public void onTeamInfoClicked() {
        SorcererTeam team = gameManager.getPlayerTeam();
        StringBuilder sb = new StringBuilder();
        sb.append(team.getInfo()).append("\n");
        for (Combatant c : team.getMembers()) {
            sb.append(c.getInfo()).append("\n\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Team Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== GameListener ====================

    @Override
    public void onDistrictChanged(District district) {
        mapPanel.setCurrentDistrict(district);
        updateStatusLabel();
        updateActionButtons();
        mapPanel.repaint();
    }

    @Override
    public void onTurnAdvanced(int turnNumber) {
        updateStatusLabel();
        log("--- Turn " + turnNumber + " ---");
        mapPanel.repaint();
    }

    @Override
    public void onBattleStarted(District district) {
        log("Battle started in " + district.getName() + "!");
    }

    @Override
    public void onBattleFinished(District district, BattleState result) {
        if (result == BattleState.VICTORY) {
            log("Victory in " + district.getName() + "! District secured.");
        } else {
            log("Defeat! Retreated to base.");
        }
        refreshMap();
    }

    @Override
    public void onMessage(String message) {
        log(message);
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
