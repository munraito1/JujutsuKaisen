package views;

import controllers.GameListener;
import controllers.GameManager;
import enums.BattleState;
import models.District;
import models.SorcererTeam;

import javax.swing.*;
import java.awt.*;

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
        super("Jujutsu Kaisen — Карта Токио");

        gameManager = new GameManager(playerTeam);
        gameManager.addListener(this);

        initUI();
        refreshMap();
        log("Добро пожаловать в Jujutsu Kaisen! Вы находитесь в «"
                + gameManager.getCurrentDistrict().getName() + "».");
        log("Нажмите на соседний район и выберите «Перемещение» для перехода.");
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));
        setResizable(false);
        getContentPane().setBackground(new Color(50, 48, 45));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 58, 55));
        topPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        statusLabel = new JLabel("Ход 1  |  Jujutsu High");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        topPanel.add(statusLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Карта районов Токио");
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        titleLabel.setForeground(new Color(180, 180, 170));
        topPanel.add(titleLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        mapPanel = new MapPanel();
        mapPanel.setSelectionListener(this);
        mapPanel.setWorldMap(gameManager.getWorldMap());
        mapPanel.setCurrentDistrict(gameManager.getCurrentDistrict());
        add(mapPanel, BorderLayout.CENTER);

        infoPanel = new DistrictInfoPanel();
        add(infoPanel, BorderLayout.EAST);

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
                "Журнал", 0, 0, null, Color.GRAY));
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
        statusLabel.setText(String.format(
                "<html>Ход <b>%d</b> &nbsp;|&nbsp; %s &nbsp;|&nbsp; " +
                "<span style='color:#e8d060'>¥%d</span> &nbsp;|&nbsp; " +
                "<span style='color:#b880f0'>ОР: %d</span> &nbsp;|&nbsp; " +
                "<span style='color:#80d080'>Команда: %d/%d</span></html>",
                gameManager.getTurnNumber(),
                gameManager.getCurrentDistrict().getName(),
                gameManager.getYuan(),
                gameManager.getGradePoints(),
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

    @Override
    public void onDistrictSelected(District district) {
        selectedDistrict = district;
        infoPanel.showDistrict(district);
        updateActionButtons();
    }

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

        log("Начинается бой в «" + gameManager.getCurrentDistrict().getName() + "»...");

        setVisible(false);

        SorcererTeam playerCopy = gameManager.getPlayerTeam();
        BattleFrame battleFrame = new BattleFrame(playerCopy, enemies,
                gameManager.getTechTree(), gameManager.getCurrentMission());
        battleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        battleFrame.setBattleEndCallback(result -> SwingUtilities.invokeLater(() -> {
            battleFrame.dispose();
            gameManager.onBattleFinished(result);
            setVisible(true);
            refreshMap();
        }));

        battleFrame.setVisible(true);
    }

    @Override
    public void onEndTurnClicked() {
        gameManager.endTurn();
        refreshMap();
    }

    @Override
    public void onBuildingsClicked() {
        BuildingFrame buildingFrame = new BuildingFrame(this, gameManager);
        buildingFrame.setVisible(true);
        updateStatusLabel();
    }

    @Override
    public void onTechTreeClicked() {
        TechTreeFrame techTreeFrame = new TechTreeFrame(this, gameManager);
        techTreeFrame.setVisible(true);
        updateStatusLabel();
    }

    @Override
    public void onTeamInfoClicked() {
        HeroManagementFrame heroFrame = new HeroManagementFrame(this, gameManager);
        heroFrame.setVisible(true);
        updateStatusLabel();
    }

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
        log("--- Ход " + turnNumber + " ---");
        mapPanel.repaint();
    }

    @Override
    public void onBattleStarted(District district) {
        log("Бой начат в «" + district.getName() + "»!");
    }

    @Override
    public void onBattleFinished(District district, BattleState result) {
        if (result == BattleState.VICTORY) {
            log("Победа в «" + district.getName() + "»! Район захвачен.");
        } else {
            log("Поражение! Отступаем на базу.");
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
