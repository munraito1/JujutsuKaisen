import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class MapGame extends JFrame {

    public static final int GRID_SIZE  = 10;
    public static final int TILE_SIZE  = 56;
    public static final int MOVE_RANGE = 3;

    // ── цвета (взяты из BattleGridPanel) ──────────────────────────────────
    private static final Color TILE_LIGHT     = new Color(55,  53,  50);
    private static final Color TILE_DARK      = new Color(44,  42,  39);
    private static final Color TILE_BORDER    = new Color(30,  28,  26);
    private static final Color PLAYER_SIDE_BG = new Color(30,  50,  70,  60);
    private static final Color MOVE_HL        = new Color(100, 180, 255, 120);
    private static final Color MOVE_HL_BORDER = new Color(80,  160, 255, 200);
    private static final Color PLAYER_COLOR   = new Color(60,  130, 230);
    private static final Color GLOW_COLOR     = new Color(255, 220, 50,  160);

    // ── состояние персонажа ────────────────────────────────────────────────
    private int charX = 1;
    private int charY = 4;

    // ── подсветка хода ─────────────────────────────────────────────────────
    private final Set<int[]> moveHighlights = new HashSet<>();
    private boolean moveMode = false;

    // ── анимация перемещения ───────────────────────────────────────────────
    private float animOffsetX = 0, animOffsetY = 0;
    private int   targetX, targetY;
    private Timer moveAnimTimer;
    private boolean isMoving = false;

    private final GridPanel gridPanel;

    public MapGame() {
        super("Jujutsu Kaisen — Карта");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(50, 48, 45));
        setLayout(new BorderLayout(0, 0));

        gridPanel = new GridPanel();
        add(gridPanel, BorderLayout.CENTER);

        JPanel bottom = buildBottomPanel();
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        setupKeys();
        showMoveHighlights(); // сразу показываем куда можно пойти
    }

    // ── нижняя панель ──────────────────────────────────────────────────────
    private JPanel buildBottomPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30, 28, 26));
        p.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel info = new JLabel();
        info.setForeground(new Color(180, 160, 100));
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        info.setText(statusText());
        p.add(info, BorderLayout.WEST);

        // обновляем текст при каждом repaint через Timer
        new Timer(100, e -> info.setText(statusText())).start();

        JLabel hint = new JLabel("WASD / стрелки — движение   |   ЛКМ по клетке — переместиться");
        hint.setForeground(new Color(120, 110, 90));
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        p.add(hint, BorderLayout.EAST);

        return p;
    }

    private String statusText() {
        return String.format("Юдзи Итадори   HP: 100 / 100   CE: 50 / 50   Позиция: (%d, %d)", charX, charY);
    }

    // ── клавиши ────────────────────────────────────────────────────────────
    private void setupKeys() {
        KeyAdapter ka = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (isMoving) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP,    KeyEvent.VK_W -> tryMove(charX,     charY - 1);
                    case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> tryMove(charX,     charY + 1);
                    case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> tryMove(charX - 1, charY);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> tryMove(charX + 1, charY);
                }
            }
        };
        addKeyListener(ka);
        gridPanel.addKeyListener(ka);
        setFocusable(true);
        gridPanel.setFocusable(true);
    }

    // ── подсветка доступных клеток ─────────────────────────────────────────
    private void showMoveHighlights() {
        moveHighlights.clear();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int dist = Math.max(Math.abs(x - charX), Math.abs(y - charY));
                if (dist > 0 && dist <= MOVE_RANGE) {
                    moveHighlights.add(new int[]{x, y});
                }
            }
        }
        moveMode = true;
    }

    // ── проверка клика по подсвеченной клетке ──────────────────────────────
    private boolean isHighlighted(int x, int y) {
        for (int[] pos : moveHighlights)
            if (pos[0] == x && pos[1] == y) return true;
        return false;
    }

    // ── попытка переместить персонажа ──────────────────────────────────────
    private void tryMove(int nx, int ny) {
        if (nx < 0 || nx >= GRID_SIZE || ny < 0 || ny >= GRID_SIZE) return;
        startMoveAnimation(nx, ny);
    }

    private void startMoveAnimation(int nx, int ny) {
        isMoving    = true;
        targetX     = nx;
        targetY     = ny;
        animOffsetX = (charX - nx) * TILE_SIZE;
        animOffsetY = (charY - ny) * TILE_SIZE;

        if (moveAnimTimer != null) moveAnimTimer.stop();
        moveAnimTimer = new Timer(16, null);
        moveAnimTimer.addActionListener(e -> {
            float speed = TILE_SIZE * 0.18f;
            animOffsetX = approach(animOffsetX, 0, speed);
            animOffsetY = approach(animOffsetY, 0, speed);
            gridPanel.repaint();
            if (Math.abs(animOffsetX) < 1f && Math.abs(animOffsetY) < 1f) {
                animOffsetX = 0; animOffsetY = 0;
                charX = targetX; charY = targetY;
                isMoving = false;
                moveAnimTimer.stop();
                showMoveHighlights();
                gridPanel.repaint();
            }
        });
        moveAnimTimer.start();
        moveHighlights.clear();
        gridPanel.repaint();
    }

    private float approach(float cur, float target, float step) {
        if (cur < target) return Math.min(cur + step, target);
        if (cur > target) return Math.max(cur - step, target);
        return cur;
    }

    // ── панель сетки ───────────────────────────────────────────────────────
    private class GridPanel extends JPanel {

        GridPanel() {
            int size = GRID_SIZE * TILE_SIZE;
            setPreferredSize(new Dimension(size, size));
            setBackground(new Color(20, 18, 16));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (isMoving) return;
                    int gx = e.getX() / TILE_SIZE;
                    int gy = e.getY() / TILE_SIZE;
                    if (gx < 0 || gx >= GRID_SIZE || gy < 0 || gy >= GRID_SIZE) return;
                    if (isHighlighted(gx, gy)) tryMove(gx, gy);
                    requestFocusInWindow();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawTiles(g2);
            drawPlayerSideTint(g2);
            drawMoveHighlights(g2);
            drawCharacter(g2);
        }

        // тайлы в шахматном порядке
        private void drawTiles(Graphics2D g) {
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    int px = x * TILE_SIZE, py = y * TILE_SIZE;
                    g.setColor((x + y) % 2 == 0 ? TILE_LIGHT : TILE_DARK);
                    g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                    g.setColor(TILE_BORDER);
                    g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // синеватый тинт на левых двух колонках (сторона игрока)
        private void drawPlayerSideTint(Graphics2D g) {
            g.setColor(PLAYER_SIDE_BG);
            g.fillRect(0, 0, 2 * TILE_SIZE, GRID_SIZE * TILE_SIZE);
        }

        // подсветка доступных клеток
        private void drawMoveHighlights(Graphics2D g) {
            for (int[] pos : moveHighlights) {
                int px = pos[0] * TILE_SIZE, py = pos[1] * TILE_SIZE;
                g.setColor(MOVE_HL);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(MOVE_HL_BORDER);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setStroke(new BasicStroke(1f));
            }
        }

        // персонаж с glow-эффектом, кружком, инициалами и HP-баром
        private void drawCharacter(Graphics2D g) {
            int drawX = targetX * TILE_SIZE + (int) animOffsetX;
            int drawY = targetY * TILE_SIZE + (int) animOffsetY;
            if (!isMoving) { drawX = charX * TILE_SIZE; drawY = charY * TILE_SIZE; }

            int margin   = 6;
            int unitSize = TILE_SIZE - margin * 2;

            // glow
            g.setColor(GLOW_COLOR);
            g.fillOval(drawX + margin - 6, drawY + margin - 6, unitSize + 12, unitSize + 12);

            // кружок
            g.setColor(PLAYER_COLOR);
            g.fillOval(drawX + margin, drawY + margin, unitSize, unitSize);
            g.setColor(PLAYER_COLOR.darker());
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(drawX + margin, drawY + margin, unitSize, unitSize);
            g.setStroke(new BasicStroke(1f));

            // инициалы
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            String label = "YI";
            g.drawString(label,
                    drawX + (TILE_SIZE - fm.stringWidth(label)) / 2,
                    drawY + TILE_SIZE / 2 + fm.getAscent() / 2 - 5);

            // HP-бар
            int barX = drawX + margin;
            int barY = drawY + TILE_SIZE - margin - 1;
            int barW = unitSize;
            g.setColor(new Color(20, 20, 20));
            g.fillRect(barX, barY, barW, 4);
            g.setColor(new Color(60, 200, 60));
            g.fillRect(barX, barY, barW, 4); // 100% HP
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapGame().setVisible(true));
    }
}
