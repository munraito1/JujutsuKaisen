package views;

import models.PlayerCharacter;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BattleGridPanel extends JPanel {

    public static final int GRID_SIZE = 10;
    public static final int TILE_SIZE = 56;

    // ── цвета (из оригинального BattleGridPanel) ────────────────────────
    private static final Color TILE_LIGHT      = new Color(55,  53,  50);
    private static final Color TILE_DARK       = new Color(44,  42,  39);
    private static final Color TILE_BORDER     = new Color(30,  28,  26);
    private static final Color PLAYER_SIDE_BG  = new Color(30,  50,  70,  60);
    private static final Color MOVE_HL         = new Color(100, 180, 255, 100);
    private static final Color MOVE_HL_BORDER  = new Color(80,  160, 255, 200);
    private static final Color PLAYER_COLOR    = new Color(60,  130, 230);
    private static final Color GLOW_COLOR      = new Color(255, 220, 50,  160);
    private static final Color DIVIDER_COLOR   = new Color(180, 160, 100, 90);

    public enum UIMode { IDLE, MOVE }

    private UIMode mode = UIMode.IDLE;
    private final Set<Position> moveHighlights = new HashSet<>();

    private final PlayerCharacter character;
    private Position charPos;

    // анимация плавного перемещения
    private float animOffsetX = 0, animOffsetY = 0;
    private Position animTarget = null;
    private Timer moveAnimTimer;
    private boolean isMoving = false;

    private Consumer<Position> onMoved; // колбэк → BattleFrame

    public BattleGridPanel(PlayerCharacter character, Position startPos) {
        this.character = character;
        this.charPos   = startPos;

        int size = GRID_SIZE * TILE_SIZE;
        setPreferredSize(new Dimension(size, size));
        setBackground(new Color(20, 18, 16));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                handleClick(e.getX() / TILE_SIZE, e.getY() / TILE_SIZE);
                requestFocusInWindow();
            }
        });

        // WASD + стрелки
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (isMoving) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP,    KeyEvent.VK_W -> tryMove(charPos.getX(),     charPos.getY() - 1);
                    case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> tryMove(charPos.getX(),     charPos.getY() + 1);
                    case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> tryMove(charPos.getX() - 1, charPos.getY());
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> tryMove(charPos.getX() + 1, charPos.getY());
                    case KeyEvent.VK_ESCAPE              -> clearHighlights();
                }
            }
        });
    }

    public void setOnMoved(Consumer<Position> callback) {
        this.onMoved = callback;
    }

    // ── управление режимом подсветки ──────────────────────────────────────
    public void showMovementRange() {
        moveHighlights.clear();
        int range = character.getMoveRange();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int dist = Math.max(Math.abs(x - charPos.getX()), Math.abs(y - charPos.getY()));
                if (dist > 0 && dist <= range) moveHighlights.add(new Position(x, y));
            }
        }
        mode = UIMode.MOVE;
        repaint();
    }

    public void clearHighlights() {
        moveHighlights.clear();
        mode = UIMode.IDLE;
        repaint();
    }

    // ── обработка кликов ──────────────────────────────────────────────────
    private void handleClick(int gx, int gy) {
        if (isMoving || gx < 0 || gx >= GRID_SIZE || gy < 0 || gy >= GRID_SIZE) return;
        Position clicked = new Position(gx, gy);
        if (mode == UIMode.MOVE && moveHighlights.contains(clicked)) {
            clearHighlights();
            startMoveAnimation(clicked);
        }
    }

    private void tryMove(int nx, int ny) {
        if (nx < 0 || nx >= GRID_SIZE || ny < 0 || ny >= GRID_SIZE) return;
        clearHighlights();
        startMoveAnimation(new Position(nx, ny));
    }

    // ── плавная анимация перемещения ─────────────────────────────────────
    private void startMoveAnimation(Position target) {
        isMoving    = true;
        animTarget  = target;
        animOffsetX = (charPos.getX() - target.getX()) * TILE_SIZE;
        animOffsetY = (charPos.getY() - target.getY()) * TILE_SIZE;

        if (moveAnimTimer != null) moveAnimTimer.stop();
        moveAnimTimer = new Timer(16, null);
        moveAnimTimer.addActionListener(e -> {
            float speed = TILE_SIZE * 0.18f;
            animOffsetX = approach(animOffsetX, 0, speed);
            animOffsetY = approach(animOffsetY, 0, speed);
            repaint();
            if (Math.abs(animOffsetX) < 1f && Math.abs(animOffsetY) < 1f) {
                animOffsetX = 0; animOffsetY = 0;
                Position prev = charPos;
                charPos    = animTarget;
                animTarget = null;
                isMoving   = false;
                moveAnimTimer.stop();
                repaint();
                if (onMoved != null) onMoved.accept(prev);
            }
        });
        moveAnimTimer.start();
    }

    private float approach(float cur, float target, float step) {
        if (cur < target) return Math.min(cur + step, target);
        if (cur > target) return Math.max(cur - step, target);
        return cur;
    }

    public Position getCharPos() { return charPos; }

    // ── отрисовка ─────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawTiles(g2);
        drawPlayerSideTint(g2);
        drawMidFieldDivider(g2);
        drawMoveHighlights(g2);
        drawCharacter(g2);
    }

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

    private void drawPlayerSideTint(Graphics2D g) {
        g.setColor(PLAYER_SIDE_BG);
        g.fillRect(0, 0, 2 * TILE_SIZE, GRID_SIZE * TILE_SIZE);
    }

    private void drawMidFieldDivider(Graphics2D g) {
        int midX = (GRID_SIZE / 2) * TILE_SIZE;
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{6f, 4f}, 0f));
        g.setColor(DIVIDER_COLOR);
        g.drawLine(midX, 4, midX, GRID_SIZE * TILE_SIZE - 4);
        g.setStroke(old);
    }

    private void drawMoveHighlights(Graphics2D g) {
        for (Position pos : moveHighlights) {
            int px = pos.getX() * TILE_SIZE, py = pos.getY() * TILE_SIZE;
            g.setColor(MOVE_HL);
            g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
            g.setColor(MOVE_HL_BORDER);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
            g.setStroke(new BasicStroke(1f));
        }
    }

    private void drawCharacter(Graphics2D g) {
        // позиция с учётом анимации
        Position base = (animTarget != null) ? animTarget : charPos;
        int drawX = base.getX() * TILE_SIZE + Math.round(animOffsetX);
        int drawY = base.getY() * TILE_SIZE + Math.round(animOffsetY);

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
        String initials = getInitials(character.getName());
        g.drawString(initials,
                drawX + (TILE_SIZE - fm.stringWidth(initials)) / 2,
                drawY + TILE_SIZE / 2 + fm.getAscent() / 2 - 5);

        // HP-бар
        int barX = drawX + margin;
        int barY = drawY + TILE_SIZE - margin - 1;
        int barW = unitSize;
        double hpRatio = (double) character.getHp() / character.getMaxHp();
        g.setColor(new Color(20, 20, 20));
        g.fillRect(barX, barY, barW, 4);
        Color hpColor = hpRatio > 0.5 ? new Color(60, 200, 60)
                : hpRatio > 0.25 ? new Color(230, 160, 30) : new Color(220, 50, 50);
        g.setColor(hpColor);
        g.fillRect(barX, barY, (int)(barW * hpRatio), 4);
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) return "" + parts[0].charAt(0) + parts[1].charAt(0);
        return name.substring(0, Math.min(2, name.length()));
    }
}
