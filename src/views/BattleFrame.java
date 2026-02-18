package views;

import controllers.AIController;
import controllers.BattleListener;
import controllers.BattleManager;
import enums.BattleState;
import models.*;
import techniques.CursedTechnique;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main battle window. Combines all panels, implements BattleListener,
 * handles technique selection popup and animation coordination.
 */
public class BattleFrame extends JFrame implements BattleListener {

    private final BattleManager battleManager;
    private final AIController aiController;

    private BattleGridPanel gridPanel;
    private UnitInfoPanel unitInfoPanel;
    private ActionPanel actionPanel;
    private TurnOrderPanel turnOrderPanel;
    private JTextArea logArea;
    private JLabel statusLabel;

    // Animation state
    private boolean waitingForAnimation = false;

    // Callback for when battle ends (used by GameFrame)
    private Consumer<BattleState> battleEndCallback;

    public BattleFrame(SorcererTeam playerTeam, SorcererTeam enemyTeam) {
        super("Jujutsu Kaisen - Tactical Battle");

        battleManager = new BattleManager();
        aiController = new AIController(battleManager);
        battleManager.addListener(this);

        initUI();

        battleManager.initBattle(playerTeam, enemyTeam);
        battleManager.startBattle();
    }

    /**
     * Set a callback to be invoked when the battle ends.
     * Used by GameFrame to handle battle results and return to the map.
     */
    public void setBattleEndCallback(Consumer<BattleState> callback) {
        this.battleEndCallback = callback;
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));
        setResizable(false);
        getContentPane().setBackground(new Color(50, 48, 45));

        // --- Top bar ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 58, 55));

        statusLabel = new JLabel("  Preparing battle...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setPreferredSize(new Dimension(280, 30));
        topPanel.add(statusLabel, BorderLayout.WEST);

        turnOrderPanel = new TurnOrderPanel();
        topPanel.add(turnOrderPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Center: grid + unit info ---
        gridPanel = new BattleGridPanel(battleManager, this);
        unitInfoPanel = new UnitInfoPanel();

        JPanel centerPanel = new JPanel(new BorderLayout(4, 0));
        centerPanel.setBackground(new Color(50, 48, 45));
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(unitInfoPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom: actions + log ---
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
                "Battle Log", 0, 0, null, Color.GRAY));
        bottomPanel.add(logScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // ==================== Button callbacks ====================

    public void onMoveClicked() {
        gridPanel.showMovementRange();
    }

    public void onAttackClicked() {
        gridPanel.showAttackRange();
    }

    public void onTechniqueClicked() {
        Combatant current = battleManager.getCurrentUnit();
        if (current == null) return;

        List<CursedTechnique> allTechs = battleManager.getAllTechniquesForUnit(current);
        List<CursedTechnique> available = battleManager.getAvailableTechniques();

        if (allTechs.isEmpty()) return;

        // Build popup menu with all techniques
        JPopupMenu popup = new JPopupMenu();
        for (CursedTechnique tech : allTechs) {
            boolean usable = available.contains(tech);
            int cd = battleManager.getCooldownRemaining(tech);

            String label = String.format("%s  (%d CE, R:%d)",
                    tech.getName(), tech.getCursedEnergyCost(), tech.getRange());
            if (cd > 0) {
                label += String.format("  [CD: %d]", cd);
            }

            JMenuItem item = new JMenuItem(label);
            item.setEnabled(usable);
            item.setFont(new Font("Arial", usable ? Font.BOLD : Font.PLAIN, 12));
            if (usable) {
                item.setForeground(new Color(100, 60, 180));
            }

            final CursedTechnique selectedTech = tech;
            item.addActionListener(e -> {
                gridPanel.showTechniqueRange(selectedTech);
            });
            popup.add(item);
        }

        // Show popup above the technique button
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

    // ==================== Action coordination ====================

    /**
     * Called after move or basic attack completes (from grid panel).
     * For techniques, this is called after the animation finishes.
     */
    public void onActionCompleted() {
        if (waitingForAnimation) return; // animation callback will handle it

        updateActionButtons();
        gridPanel.repaint();

        Combatant current = battleManager.getCurrentUnit();
        if (current != null) unitInfoPanel.showUnit(current);

        // Auto-end turn if both moved and acted
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

    public void showUnitInfo(Combatant unit) {
        unitInfoPanel.showUnit(unit);
    }

    private void updateActionButtons() {
        Combatant current = battleManager.getCurrentUnit();
        if (current == null || !battleManager.isPlayerUnit(current)) {
            actionPanel.disableAll();
            return;
        }

        boolean canMove = !battleManager.isCurrentUnitMoved();
        boolean canAct = !battleManager.isCurrentUnitActed();
        boolean hasTech = battleManager.hasUsableTechniques(current);

        String techLabel = "Techniques";
        List<CursedTechnique> techs = battleManager.getAllTechniquesForUnit(current);
        if (techs.size() == 1) {
            techLabel = techs.get(0).getName();
        }

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

    // ==================== BattleListener ====================

    @Override
    public void onBattleStarted() {
        log("Battle begins!");
    }

    @Override
    public void onTurnStarted(Combatant unit) {
        String side = battleManager.isPlayerUnit(unit) ? "[ALLY]" : "[ENEMY]";
        statusLabel.setText(String.format("  Round %d  -  %s %s",
                battleManager.getRoundNumber(), side, unit.getName()));
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
        log(unit.getName() + " moves " + from + " -> " + to);
        gridPanel.repaint();
    }

    @Override
    public void onUnitAttacked(Combatant attacker, Combatant target, int damage) {
        log(String.format("%s attacks %s for %d dmg [HP: %d/%d]",
                attacker.getName(), target.getName(), damage,
                target.getHp(), target.getMaxHp()));
        unitInfoPanel.showUnit(target);
        gridPanel.repaint();
    }

    @Override
    public void onTechniqueUsed(Combatant user, Combatant target, String techniqueName,
                                int damage, String animationType, Position targetPos) {
        log(String.format("%s uses %s on %s for %d dmg [HP: %d/%d]",
                user.getName(), techniqueName, target.getName(), damage,
                target.getHp(), target.getMaxHp()));
        unitInfoPanel.showUnit(target);
        gridPanel.repaint();

        // Play animation if applicable
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
        log(unit.getName() + " takes a defensive stance!");
        gridPanel.repaint();
    }

    @Override
    public void onUnitDefeated(Combatant unit) {
        log(">>> " + unit.getName() + " has been DEFEATED! <<<");
        gridPanel.repaint();
    }

    @Override
    public void onBattleEnded(BattleState result) {
        String message = result == BattleState.VICTORY
                ? "VICTORY! All enemies defeated!"
                : "DEFEAT... All allies have fallen.";
        log("========== " + message + " ==========");
        statusLabel.setText("  " + message);
        actionPanel.disableAll();
        gridPanel.clearHighlights();
        gridPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Battle Over",
                    result == BattleState.VICTORY
                            ? JOptionPane.INFORMATION_MESSAGE
                            : JOptionPane.WARNING_MESSAGE);

            if (battleEndCallback != null) {
                battleEndCallback.accept(result);
            }
        });
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
