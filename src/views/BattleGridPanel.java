package views;

import controllers.BattleManager;
import models.Combatant;
import techniques.CursedTechnique;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Renders the battle grid, handles mouse interaction, and plays combat animations.
 */
public class BattleGridPanel extends JPanel {

    public static final int TILE_SIZE = 56;

    private final BattleManager battleManager;
    private final BattleFrame parentFrame;

    // Highlighting
    private final Set<Position> moveHighlights = new HashSet<>();
    private final Set<Position> attackHighlights = new HashSet<>();

    public enum UIMode { IDLE, MOVE, ATTACK, TECHNIQUE }
    private UIMode mode = UIMode.IDLE;

    // Selected technique for TECHNIQUE mode
    private CursedTechnique selectedTechnique = null;

    // --- Animation ---
    private String animationType = null;
    private Position animationPos = null;
    private int animFrame = 0;
    private Timer animTimer = null;
    private Runnable animationCallback = null;

    // Colors
    private static final Color TILE_LIGHT   = new Color(232, 225, 210);
    private static final Color TILE_DARK    = new Color(212, 205, 190);
    private static final Color MOVE_HL      = new Color(100, 180, 255, 90);
    private static final Color ATTACK_HL    = new Color(255, 100, 100, 90);
    private static final Color TECH_HL      = new Color(180, 100, 255, 90);
    private static final Color PLAYER_COLOR = new Color(50, 120, 220);
    private static final Color ENEMY_COLOR  = new Color(200, 50, 50);
    private static final Color GLOW_COLOR   = new Color(255, 220, 50, 150);

    public BattleGridPanel(BattleManager manager, BattleFrame parent) {
        this.battleManager = manager;
        this.parentFrame = parent;

        int size = BattleManager.GRID_SIZE * TILE_SIZE;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    // ==================== Mode switches ====================

    public void showMovementRange() {
        clearHighlightsInternal();
        moveHighlights.addAll(battleManager.getMovablePositions());
        mode = UIMode.MOVE;
        repaint();
    }

    public void showAttackRange() {
        clearHighlightsInternal();
        for (Combatant target : battleManager.getAttackableTargets()) {
            Position p = battleManager.getUnitPosition(target);
            if (p != null) attackHighlights.add(p);
        }
        mode = UIMode.ATTACK;
        repaint();
    }

    public void showTechniqueRange(CursedTechnique tech) {
        clearHighlightsInternal();
        this.selectedTechnique = tech;
        for (Combatant target : battleManager.getTechniqueTargets(tech)) {
            Position p = battleManager.getUnitPosition(target);
            if (p != null) attackHighlights.add(p);
        }
        mode = UIMode.TECHNIQUE;
        repaint();
    }

    public void clearHighlights() {
        clearHighlightsInternal();
        repaint();
    }

    private void clearHighlightsInternal() {
        moveHighlights.clear();
        attackHighlights.clear();
        mode = UIMode.IDLE;
        selectedTechnique = null;
    }

    // ==================== Animation ====================

    public void playAnimation(String type, Position pos, Runnable onComplete) {
        this.animationType = type;
        this.animationPos = pos;
        this.animFrame = 0;
        this.animationCallback = onComplete;

        int totalFrames = getAnimationFrameCount(type);
        if (totalFrames <= 0) {
            if (onComplete != null) onComplete.run();
            return;
        }

        animTimer = new Timer(30, e -> {
            animFrame++;
            repaint();
            if (animFrame >= totalFrames) {
                stopAnimation();
            }
        });
        animTimer.start();
    }

    private void stopAnimation() {
        if (animTimer != null) {
            animTimer.stop();
            animTimer = null;
        }
        animationType = null;
        animationPos = null;
        animFrame = 0;
        repaint();
        if (animationCallback != null) {
            Runnable cb = animationCallback;
            animationCallback = null;
            cb.run();
        }
    }

    public boolean isAnimating() {
        return animTimer != null;
    }

    private int getAnimationFrameCount(String type) {
        if ("BLACK_FLASH".equals(type)) return 20;
        if ("HIT".equals(type)) return 7;
        return 0;
    }

    // ==================== Click handling ====================

    private void handleClick(int px, int py) {
        if (isAnimating()) return; // ignore clicks during animation

        int gx = px / TILE_SIZE;
        int gy = py / TILE_SIZE;
        if (gx < 0 || gx >= BattleManager.GRID_SIZE || gy < 0 || gy >= BattleManager.GRID_SIZE) return;

        Position clicked = new Position(gx, gy);

        switch (mode) {
            case IDLE:
                Combatant unit = battleManager.getUnitAt(clicked);
                if (unit != null) parentFrame.showUnitInfo(unit);
                break;

            case MOVE:
                if (moveHighlights.contains(clicked)) {
                    battleManager.moveUnit(clicked);
                    clearHighlightsInternal();
                    parentFrame.onActionCompleted();
                }
                break;

            case ATTACK:
                Combatant target = battleManager.getUnitAt(clicked);
                if (target != null && attackHighlights.contains(clicked)) {
                    battleManager.basicAttack(target);
                    clearHighlightsInternal();
                    parentFrame.onActionCompleted();
                }
                break;

            case TECHNIQUE:
                Combatant techTarget = battleManager.getUnitAt(clicked);
                if (techTarget != null && attackHighlights.contains(clicked) && selectedTechnique != null) {
                    CursedTechnique tech = selectedTechnique;
                    clearHighlightsInternal();
                    battleManager.useTechnique(tech, techTarget);
                    // onActionCompleted is called from BattleFrame after animation
                }
                break;
        }
        repaint();
    }

    // ==================== Rendering ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw tiles
        for (int x = 0; x < BattleManager.GRID_SIZE; x++) {
            for (int y = 0; y < BattleManager.GRID_SIZE; y++) {
                drawTile(g2, x, y);
            }
        }

        // Draw units
        for (Map.Entry<Combatant, Position> entry : battleManager.getUnitPositions().entrySet()) {
            Combatant unit = entry.getKey();
            Position pos = entry.getValue();
            if (unit.isAlive()) {
                drawUnit(g2, unit, pos);
            }
        }

        // Draw animation overlay
        drawAnimation(g2);
    }

    private void drawTile(Graphics2D g, int x, int y) {
        int px = x * TILE_SIZE;
        int py = y * TILE_SIZE;
        Position pos = new Position(x, y);

        g.setColor((x + y) % 2 == 0 ? TILE_LIGHT : TILE_DARK);
        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        if (moveHighlights.contains(pos)) {
            g.setColor(MOVE_HL);
            g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
        } else if (attackHighlights.contains(pos)) {
            g.setColor(mode == UIMode.TECHNIQUE ? TECH_HL : ATTACK_HL);
            g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
        }

        g.setColor(new Color(180, 175, 165));
        g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void drawUnit(Graphics2D g, Combatant unit, Position pos) {
        int px = pos.getX() * TILE_SIZE;
        int py = pos.getY() * TILE_SIZE;
        int margin = 6;
        int unitSize = TILE_SIZE - margin * 2;

        boolean isPlayer = battleManager.isPlayerUnit(unit);
        Color unitColor = isPlayer ? PLAYER_COLOR : ENEMY_COLOR;

        if (unit == battleManager.getCurrentUnit()) {
            g.setColor(GLOW_COLOR);
            g.fillOval(px + margin - 4, py + margin - 4, unitSize + 8, unitSize + 8);
        }

        g.setColor(unitColor);
        g.fillOval(px + margin, py + margin, unitSize, unitSize);
        g.setColor(unitColor.darker());
        g.drawOval(px + margin, py + margin, unitSize, unitSize);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        String initials = getInitials(unit.getName());
        FontMetrics fm = g.getFontMetrics();
        int textX = px + (TILE_SIZE - fm.stringWidth(initials)) / 2;
        int textY = py + TILE_SIZE / 2 + fm.getAscent() / 2 - 4;
        g.drawString(initials, textX, textY);

        // HP bar
        int barX = px + margin;
        int barY = py + TILE_SIZE - margin;
        int barW = unitSize;
        int barH = 4;
        double hpRatio = (double) unit.getHp() / unit.getMaxHp();

        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barW, barH);

        Color hpColor = hpRatio > 0.5 ? new Color(60, 180, 60) : hpRatio > 0.25 ? Color.ORANGE : Color.RED;
        g.setColor(hpColor);
        g.fillRect(barX, barY, (int) (barW * hpRatio), barH);
    }

    // ==================== Animation rendering ====================

    private void drawAnimation(Graphics2D g) {
        if (animationType == null || animationPos == null) return;

        int cx = animationPos.getX() * TILE_SIZE + TILE_SIZE / 2;
        int cy = animationPos.getY() * TILE_SIZE + TILE_SIZE / 2;

        if ("BLACK_FLASH".equals(animationType)) {
            drawBlackFlash(g, cx, cy);
        } else if ("HIT".equals(animationType)) {
            drawHitEffect(g, cx, cy);
        }
    }

    private void drawBlackFlash(Graphics2D g, int cx, int cy) {
        Stroke oldStroke = g.getStroke();

        if (animFrame < 5) {
            // Phase 1: Screen dims
            float alpha = animFrame / 5f * 0.5f;
            g.setColor(new Color(0, 0, 0, (int) (255 * alpha)));
            g.fillRect(0, 0, getWidth(), getHeight());

        } else if (animFrame < 9) {
            // Phase 2: Bright impact flash
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, getWidth(), getHeight());

            int f = animFrame - 5;
            int radius = 12 + f * 18;
            float flashA = 1.0f - f / 4f;

            // Yellow/white flash
            g.setColor(new Color(255, 255, 180, (int) (255 * flashA)));
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // Bright core
            int core = 6 + f * 6;
            g.setColor(new Color(255, 255, 255, (int) (220 * flashA)));
            g.fillOval(cx - core, cy - core, core * 2, core * 2);

        } else if (animFrame < 16) {
            // Phase 3: Black/red distortion rings
            int f = animFrame - 9;
            float fade = 1.0f - f / 7f;

            g.setColor(new Color(0, 0, 0, (int) (100 * fade)));
            g.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i <= f; i++) {
                int ringR = 18 + (f - i) * 22 + i * 12;
                float ringA = fade * (1.0f - i / (float) (f + 1));

                // Black ring
                g.setColor(new Color(0, 0, 0, (int) (220 * ringA)));
                g.setStroke(new BasicStroke(3f));
                g.drawOval(cx - ringR, cy - ringR, ringR * 2, ringR * 2);

                // Red inner ring
                int innerR = ringR - 6;
                g.setColor(new Color(255, 40, 40, (int) (170 * ringA)));
                g.setStroke(new BasicStroke(2f));
                g.drawOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);
            }

        } else {
            // Phase 4: Fade out
            int f = animFrame - 16;
            float fade = 1.0f - f / 4f;
            g.setColor(new Color(0, 0, 0, (int) (50 * Math.max(0, fade))));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // "BLACK FLASH!" text during mid-frames
        if (animFrame >= 6 && animFrame <= 14) {
            float textA = animFrame < 10 ? 1.0f : 1.0f - (animFrame - 10) / 5f;
            g.setColor(new Color(255, 40, 40, (int) (255 * Math.max(0, textA))));
            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            String text = "BLACK FLASH!";
            g.drawString(text, cx - fm.stringWidth(text) / 2, cy - 45);

            // Damage multiplier text
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(new Color(255, 220, 100, (int) (200 * Math.max(0, textA))));
            String mult = "x2.5";
            FontMetrics fm2 = g.getFontMetrics();
            g.drawString(mult, cx - fm2.stringWidth(mult) / 2, cy - 28);
        }

        g.setStroke(oldStroke);
    }

    private void drawHitEffect(Graphics2D g, int cx, int cy) {
        if (animFrame < 2) {
            // White flash
            int radius = 14 + animFrame * 12;
            int alpha = 220 - animFrame * 60;
            g.setColor(new Color(255, 255, 255, Math.max(0, alpha)));
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        } else if (animFrame < 5) {
            // Orange ring
            int f = animFrame - 2;
            int radius = 20 + f * 14;
            float a = 1.0f - f / 3f;
            g.setColor(new Color(255, 180, 50, (int) (200 * a)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
            g.setStroke(new BasicStroke(1f));
        } else {
            // Fade
            int f = animFrame - 5;
            int radius = 35 + f * 8;
            float a = 1.0f - f / 3f;
            g.setColor(new Color(255, 200, 100, (int) (80 * Math.max(0, a))));
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return "" + parts[0].charAt(0) + parts[1].charAt(0);
        }
        return name.substring(0, Math.min(2, name.length()));
    }
}
