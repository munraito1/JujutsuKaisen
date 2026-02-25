package views;

import controllers.AIController;
import controllers.BattleListener;
import controllers.BattleManager;
import enums.BattleState;
import models.*;
import models.Mission;
import models.TechTree;
import techniques.CursedTechnique;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

public class BattleFrame extends JFrame implements BattleListener {

    private final BattleManager battleManager;
    private final AIController aiController;

    private BattleGridPanel gridPanel;
    private UnitInfoPanel unitInfoPanel;
    private ActionPanel actionPanel;
    private TurnOrderPanel turnOrderPanel;
    private JTextArea logArea;
    private JLabel statusLabel;

    private boolean waitingForAnimation = false;

    private Consumer<BattleState> battleEndCallback;

    private Mission currentMission;

    public BattleFrame(SorcererTeam playerTeam, SorcererTeam enemyTeam) {
        this(playerTeam, enemyTeam, null, null);
    }

    public BattleFrame(SorcererTeam playerTeam, SorcererTeam enemyTeam, TechTree techTree) {
        this(playerTeam, enemyTeam, techTree, null);
    }

    public BattleFrame(SorcererTeam playerTeam, SorcererTeam enemyTeam,
                       TechTree techTree, Mission mission) {
        super("Jujutsu Kaisen — Тактический бой");

        this.currentMission = mission;

        battleManager = new BattleManager();
        if (techTree != null) battleManager.setTechTree(techTree);
        if (mission != null && mission.hasTurnLimit()) {
            battleManager.setMaxRounds(mission.getTurnLimit());
        }
        aiController = new AIController(battleManager);
        battleManager.addListener(this);

        initUI();

        battleManager.initBattle(playerTeam, enemyTeam);
        battleManager.startBattle();
    }

    public void setBattleEndCallback(Consumer<BattleState> callback) {
        this.battleEndCallback = callback;
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));
        setResizable(false);
        getContentPane().setBackground(new Color(50, 48, 45));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 58, 55));

        statusLabel = new JLabel("  Подготовка к бою...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setPreferredSize(new Dimension(280, 30));
        topPanel.add(statusLabel, BorderLayout.WEST);

        turnOrderPanel = new TurnOrderPanel();
        topPanel.add(turnOrderPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new BattleGridPanel(battleManager, this);
        unitInfoPanel = new UnitInfoPanel();

        JPanel centerPanel = new JPanel(new BorderLayout(4, 0));
        centerPanel.setBackground(new Color(50, 48, 45));
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(unitInfoPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        actionPanel = new ActionPanel(this);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);

        logArea = new JTextArea(7, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(30, 28, 25));
        logArea.setForeground(new Color(200, 200, 190));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Журнал боя", 0, 0, null, Color.GRAY));
        bottomPanel.add(logScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(
                e -> gridPanel.clearHighlights(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setLocationRelativeTo(null);
    }

    public void onMoveClicked()    { gridPanel.showMovementRange(); }
    public void onAttackClicked()  { gridPanel.showAttackRange(); }

    public void onTechniqueClicked() {
        Combatant current = battleManager.getCurrentUnit();
        if (current == null) return;

        List<CursedTechnique> allTechs  = battleManager.getAllTechniquesForUnit(current);
        List<CursedTechnique> available = battleManager.getAvailableTechniques();
        if (allTechs.isEmpty()) return;

        JPopupMenu popup = new JPopupMenu();
        for (CursedTechnique tech : allTechs) {
            boolean usable = available.contains(tech);
            int cd = battleManager.getCooldownRemaining(tech);

            String label = String.format("%s  (%d ПЭ, Д:%d)",
                    tech.getName(), tech.getCursedEnergyCost(), tech.getRange());
            if (cd > 0) label += String.format("  [КД: %d]", cd);

            JMenuItem item = new JMenuItem(label);
            item.setEnabled(usable);
            item.setFont(new Font("Arial", usable ? Font.BOLD : Font.PLAIN, 12));
            if (usable) item.setForeground(new Color(100, 60, 180));

            final CursedTechnique selectedTech = tech;
            item.addActionListener(e -> gridPanel.showTechniqueRange(selectedTech));
            popup.add(item);
        }

        JButton btn = actionPanel.getTechniqueButton();
        popup.show(btn, 0, -popup.getPreferredSize().height);
    }

    public void onDefendClicked() {
        gridPanel.clearHighlights();
        battleManager.defend();
        onActionCompleted();
    }

    public void onEndTurnClicked() {
        gridPanel.clearHighlights();
        battleManager.endTurn();
    }

    public void onActionCompleted() {
        if (waitingForAnimation) return;

        updateActionButtons();
        gridPanel.repaint();

        Combatant current = battleManager.getCurrentUnit();
        if (current != null) unitInfoPanel.showUnit(current);

        if (battleManager.isCurrentUnitMoved() && battleManager.isCurrentUnitActed()) {
            Timer timer = new Timer(300, e -> {
                if (battleManager.getState() != BattleState.VICTORY
                        && battleManager.getState() != BattleState.DEFEAT) {
                    battleManager.endTurn();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void showUnitInfo(Combatant unit) { unitInfoPanel.showUnit(unit); }

    private void updateActionButtons() {
        Combatant current = battleManager.getCurrentUnit();
        if (current == null || !battleManager.isPlayerUnit(current)) {
            actionPanel.disableAll();
            return;
        }

        boolean canMove = !battleManager.isCurrentUnitMoved();
        boolean canAct  = !battleManager.isCurrentUnitActed();
        boolean hasTech = battleManager.hasUsableTechniques(current);

        String techLabel = "Техника";
        List<CursedTechnique> techs = battleManager.getAllTechniquesForUnit(current);
        if (techs.size() == 1) techLabel = techs.get(0).getName();

        actionPanel.updateButtons(canMove, canAct, hasTech, techLabel);
    }

    private void processEnemyTurn() {
        actionPanel.disableAll();
        Timer timer = new Timer(600, e -> {
            if (battleManager.getState() == BattleState.VICTORY
                    || battleManager.getState() == BattleState.DEFEAT) return;
            aiController.executeTurn();
            gridPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void onBattleStarted() {
        log("Бой начинается!");
    }

    @Override
    public void onTurnStarted(Combatant unit) {
        String side = battleManager.isPlayerUnit(unit) ? "[СОЮЗНИК]" : "[ВРАГ]";
        String missionInfo = "";
        if (currentMission != null) {
            missionInfo = " | " + currentMission.getDisplayName();
            int rem = battleManager.getRoundsRemaining();
            if (rem >= 0) missionInfo += " ⏱" + rem;
        }
        statusLabel.setText(String.format("  Раунд %d%s  —  %s %s",
                battleManager.getRoundNumber(), missionInfo, side, unit.getName()));
        turnOrderPanel.updateTurnOrder(battleManager.getTurnOrder(), unit, battleManager);
        unitInfoPanel.showUnit(unit);
        gridPanel.repaint();

        if (battleManager.isPlayerUnit(unit)) {
            updateActionButtons();
        } else {
            processEnemyTurn();
        }
    }

    @Override
    public void onUnitMoved(Combatant unit, Position from, Position to) {
        log(unit.getName() + " перемещается " + from + " → " + to);
        gridPanel.repaint();
    }

    @Override
    public void onUnitAttacked(Combatant attacker, Combatant target, int damage, boolean blackFlash) {
        if (blackFlash) {
            log(String.format("⚡ ЧЁРНАЯ МОЛНИЯ! %s атакует %s на %d ед. [HP: %d/%d]",
                    attacker.getName(), target.getName(), damage,
                    target.getHp(), target.getMaxHp()));
        } else {
            log(String.format("%s атакует %s на %d ед. [HP: %d/%d]",
                    attacker.getName(), target.getName(), damage,
                    target.getHp(), target.getMaxHp()));
        }
        unitInfoPanel.showUnit(target);
        gridPanel.repaint();

        if (blackFlash) {
            Position targetPos = battleManager.getUnitPosition(target);
            if (targetPos != null) {
                waitingForAnimation = true;
                actionPanel.disableAll();
                gridPanel.playAnimation("BLACK_FLASH", targetPos, () -> {
                    waitingForAnimation = false;
                    onActionCompleted();
                });
                return;
            }
        }
        onActionCompleted();
    }

    @Override
    public void onTechniqueUsed(Combatant user, Combatant target, String techniqueName,
                                int damage, String animationType, Position targetPos) {
        log(String.format("%s применяет «%s» на %s: %d ед. [HP: %d/%d]",
                user.getName(), techniqueName, target.getName(), damage,
                target.getHp(), target.getMaxHp()));
        unitInfoPanel.showUnit(target);
        gridPanel.repaint();

        if (animationType != null && !"NONE".equals(animationType) && targetPos != null) {
            waitingForAnimation = true;
            actionPanel.disableAll();
            gridPanel.playAnimation(animationType, targetPos, () -> {
                waitingForAnimation = false;
                onActionCompleted();
            });
        } else {
            onActionCompleted();
        }
    }

    @Override
    public void onUnitDefended(Combatant unit) {
        log(unit.getName() + " занимает оборонительную позицию!");
        gridPanel.repaint();
    }

    @Override
    public void onUnitDefeated(Combatant unit) {
        log(">>> " + unit.getName() + " ПОВЕРЖЕН! <<<");
        gridPanel.repaint();
    }

    @Override
    public void onBattleEnded(BattleState result) {
        String message = result == BattleState.VICTORY
                ? "ПОБЕДА! Все враги повержены!"
                : "ПОРАЖЕНИЕ... Все союзники пали.";
        log("========== " + message + " ==========");
        statusLabel.setText("  " + message);
        actionPanel.disableAll();
        gridPanel.clearHighlights();
        gridPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            showBattleResultDialog(result, message);

            if (battleEndCallback != null) {
                battleEndCallback.accept(result);
            }
        });
    }

    @Override
    public void onMessage(String message) {
        log(message);
    }

    private void showBattleResultDialog(BattleState result, String message) {
        boolean victory = (result == BattleState.VICTORY);

        JDialog dialog = new JDialog(this, "Бой завершён", true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        Color bgColor = victory ? new Color(30, 55, 30) : new Color(55, 25, 25);
        Color accentColor = victory ? new Color(80, 200, 80) : new Color(220, 60, 60);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(24, 36, 20, 36)));

        JLabel icon = new JLabel(victory ? "✦" : "✖", SwingConstants.CENTER);
        icon.setFont(new Font("Arial", Font.PLAIN, 42));
        icon.setForeground(accentColor);

        JLabel text = new JLabel(message, SwingConstants.CENTER);
        text.setFont(new Font("Arial", Font.BOLD, 16));
        text.setForeground(victory ? new Color(180, 240, 180) : new Color(240, 160, 160));

        JButton ok = new JButton("Продолжить");
        ok.setFont(new Font("Arial", Font.BOLD, 13));
        ok.setBackground(accentColor.darker());
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.setOpaque(true);
        ok.setBorderPainted(false);
        ok.setPreferredSize(new Dimension(150, 36));
        ok.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(bgColor);
        btnPanel.add(ok);

        panel.add(icon, BorderLayout.NORTH);
        panel.add(text, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
