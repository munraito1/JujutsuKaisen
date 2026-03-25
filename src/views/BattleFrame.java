package views;

import models.PlayerCharacter;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class BattleFrame extends JFrame {

    private final PlayerCharacter character;
    private final BattleGridPanel gridPanel;
    private final UnitInfoPanel   unitInfoPanel;
    private final ActionPanel     actionPanel;
    private final JTextArea       logArea;
    private final JLabel          statusLabel;

    public BattleFrame() {
        super("Jujutsu Kaisen — Тактический бой");

        character = new PlayerCharacter(
                "Юдзи Итадори", "Студент 1-го курса",
                500, 100, 1,
                80, 30, 90,
                3, 1
        );

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));
        setResizable(false);
        getContentPane().setBackground(new Color(50, 48, 45));

        // ── верхняя панель ────────────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 58, 55));

        statusLabel = new JLabel("  [СОЮЗНИК] Юдзи Итадори");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setPreferredSize(new Dimension(320, 30));
        topPanel.add(statusLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // ── центр: сетка + панель юнита ───────────────────────────────────
        gridPanel     = new BattleGridPanel(character, new Position(1, 4));
        unitInfoPanel = new UnitInfoPanel();
        unitInfoPanel.showUnit(character);

        JPanel centerPanel = new JPanel(new BorderLayout(4, 0));
        centerPanel.setBackground(new Color(50, 48, 45));
        centerPanel.add(gridPanel,     BorderLayout.CENTER);
        centerPanel.add(unitInfoPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        // ── нижняя панель: кнопки + лог ───────────────────────────────────
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
                "Журнал", 0, 0, null, Color.GRAY));
        bottomPanel.add(logScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ESC — сбросить подсветку
        getRootPane().registerKeyboardAction(
                e -> gridPanel.clearHighlights(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // колбэк — персонаж переместился
        gridPanel.setOnMoved(prev -> {
            Position cur = gridPanel.getCharPos();
            log(String.format("%s перемещается %s → %s",
                    character.getName(), prev, cur));
            statusLabel.setText(String.format("  [СОЮЗНИК] %s   Позиция: %s",
                    character.getName(), cur));
        });

        pack();
        setLocationRelativeTo(null);

        log("Бой начинается!");
        log(character.getName() + " готов к действию.");
        log("Используй WASD / стрелки или нажми «Движение» и кликни по клетке.");

        // фокус на сетку для клавиш
        SwingUtilities.invokeLater(gridPanel::requestFocusInWindow);
    }

    public void onMoveClicked() {
        gridPanel.showMovementRange();
        log("Выбери клетку для перемещения (или нажми ESC для отмены).");
    }

    private static final int MAX_LOG = 150;

    private void log(String message) {
        logArea.append(message + "\n");
        javax.swing.text.Document doc = logArea.getDocument();
        javax.swing.text.Element root = doc.getDefaultRootElement();
        while (root.getElementCount() > MAX_LOG) {
            javax.swing.text.Element first = root.getElement(0);
            try { doc.remove(first.getStartOffset(), first.getEndOffset()); }
            catch (javax.swing.text.BadLocationException ignored) { break; }
        }
        logArea.setCaretPosition(doc.getLength());
    }
}
