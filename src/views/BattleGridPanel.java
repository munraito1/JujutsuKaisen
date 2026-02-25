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

public class BattleGridPanel extends JPanel {

    public static final int TILE_SIZE = 56;

    private final BattleManager battleManager;
    private final BattleFrame parentFrame;

    private final Set<Position> moveHighlights = new HashSet<>();
    private final Set<Position> attackHighlights = new HashSet<>();

    public enum UIMode { IDLE, MOVE, ATTACK, TECHNIQUE }
    private UIMode mode = UIMode.IDLE;

    private CursedTechnique selectedTechnique = null;

    private String animationType = null;
    private Position animationPos = null;
    private int animFrame = 0;
    private Timer animTimer = null;
    private Runnable animationCallback = null;

    private static final Color TILE_LIGHT      = new Color(55, 53, 50);
    private static final Color TILE_DARK       = new Color(44, 42, 39);
    private static final Color PLAYER_SIDE_BG  = new Color(30, 50, 70, 60);
    private static final Color ENEMY_SIDE_BG   = new Color(70, 30, 30, 60);
    private static final Color MOVE_HL         = new Color(100, 180, 255, 100);
    private static final Color ATTACK_HL       = new Color(255, 100, 100, 110);
    private static final Color TECH_HL         = new Color(180, 100, 255, 110);
    private static final Color PLAYER_COLOR    = new Color(60, 130, 230);
    private static final Color ENEMY_COLOR     = new Color(210, 55, 55);
    private static final Color GLOW_COLOR      = new Color(255, 220, 50, 160);
    private static final Color DIVIDER_COLOR   = new Color(180, 160, 100, 90);
    private static final Color DEFEND_COLOR    = new Color(80, 200, 140);

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
        if ("HIT".equals(type))         return 7;
        if ("TECHNIQUE".equals(type))   return 15;
        return 0;
    }

    private void handleClick(int px, int py) {
        if (isAnimating()) return;

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
                }
                break;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int x = 0; x < BattleManager.GRID_SIZE; x++) {
            for (int y = 0; y < BattleManager.GRID_SIZE; y++) {
                drawTile(g2, x, y);
            }
        }

        drawSideTints(g2);

        drawMidFieldDivider(g2);

        for (Map.Entry<Combatant, Position> entry : battleManager.getUnitPositions().entrySet()) {
            Combatant unit = entry.getKey();
            Position pos = entry.getValue();
            if (unit.isAlive()) {
                drawUnit(g2, unit, pos);
            }
        }

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

        g.setColor(new Color(30, 28, 26));
        g.drawRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    private void drawSideTints(Graphics2D g) {
        int rows = BattleManager.GRID_SIZE;

        g.setColor(PLAYER_SIDE_BG);
        g.fillRect(0, 0, 2 * TILE_SIZE, rows * TILE_SIZE);

        g.setColor(ENEMY_SIDE_BG);
        g.fillRect(8 * TILE_SIZE, 0, 2 * TILE_SIZE, rows * TILE_SIZE);
    }

    private void drawMidFieldDivider(Graphics2D g) {
        int w = BattleManager.GRID_SIZE * TILE_SIZE;
        int midX = (BattleManager.GRID_SIZE / 2) * TILE_SIZE;

        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{6f, 4f}, 0f));
        g.setColor(DIVIDER_COLOR);
        g.drawLine(midX, 4, midX, w - 4);
        g.setStroke(old);
    }

    private void drawUnit(Graphics2D g, Combatant unit, Position pos) {
        int px = pos.getX() * TILE_SIZE;
        int py = pos.getY() * TILE_SIZE;
        int margin = 6;
        int unitSize = TILE_SIZE - margin * 2;

        boolean isPlayer = battleManager.isPlayerUnit(unit);
        boolean isCurrent = (unit == battleManager.getCurrentUnit());
        boolean isDefending = battleManager.isDefending(unit);
        Color unitColor = isPlayer ? PLAYER_COLOR : ENEMY_COLOR;

        if (isCurrent) {
            g.setColor(GLOW_COLOR);
            g.fillOval(px + margin - 5, py + margin - 5, unitSize + 10, unitSize + 10);
        }

        if (isDefending) {
            g.setColor(DEFEND_COLOR);
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(px + margin - 3, py + margin - 3, unitSize + 6, unitSize + 6);
            g.setStroke(new BasicStroke(1f));
        }

        g.setColor(unitColor);
        g.fillOval(px + margin, py + margin, unitSize, unitSize);
        g.setColor(unitColor.darker());
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(px + margin, py + margin, unitSize, unitSize);
        g.setStroke(new BasicStroke(1f));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String initials = getInitials(unit.getName());
        FontMetrics fm = g.getFontMetrics();
        int textX = px + (TILE_SIZE - fm.stringWidth(initials)) / 2;
        int textY = py + TILE_SIZE / 2 + fm.getAscent() / 2 - 5;
        g.drawString(initials, textX, textY);

        int barX = px + margin;
        int barY = py + TILE_SIZE - margin - 1;
        int barW = unitSize;
        int barH = 4;
        double hpRatio = (double) unit.getHp() / unit.getMaxHp();

        g.setColor(new Color(20, 20, 20));
        g.fillRect(barX, barY, barW, barH);
        Color hpColor = hpRatio > 0.5 ? new Color(60, 200, 60)
                : hpRatio > 0.25 ? new Color(230, 160, 30) : new Color(220, 50, 50);
        g.setColor(hpColor);
        g.fillRect(barX, barY, (int) (barW * hpRatio), barH);

        if (isDefending) {
            int bx = px + TILE_SIZE - margin - 4;
            int by = py + margin + 1;
            g.setColor(DEFEND_COLOR);
            g.fillOval(bx - 6, by - 1, 12, 12);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 8));
            FontMetrics fm2 = g.getFontMetrics();
            g.drawString("D", bx - fm2.stringWidth("D") / 2, by + 8);
        }
    }

    private void drawAnimation(Graphics2D g) {
        if (animationType == null || animationPos == null) return;

        int cx = animationPos.getX() * TILE_SIZE + TILE_SIZE / 2;
        int cy = animationPos.getY() * TILE_SIZE + TILE_SIZE / 2;

        if ("BLACK_FLASH".equals(animationType)) {
            drawBlackFlash(g, cx, cy);
        } else if ("HIT".equals(animationType)) {
            drawHitEffect(g, cx, cy);
        } else if ("TECHNIQUE".equals(animationType)) {
            drawTechniqueEffect(g, cx, cy);
        }
    }

    private void drawBlackFlash(Graphics2D g, int cx, int cy) {
        Stroke oldStroke = g.getStroke();

        if (animFrame < 5) {
            float alpha = animFrame / 5f * 0.5f;
            g.setColor(new Color(0, 0, 0, (int) (255 * alpha)));
            g.fillRect(0, 0, getWidth(), getHeight());

        } else if (animFrame < 9) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, getWidth(), getHeight());

            int f = animFrame - 5;
            int radius = 12 + f * 18;
            float flashA = 1.0f - f / 4f;

            g.setColor(new Color(255, 255, 180, (int) (255 * flashA)));
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

            int core = 6 + f * 6;
            g.setColor(new Color(255, 255, 255, (int) (220 * flashA)));
            g.fillOval(cx - core, cy - core, core * 2, core * 2);

        } else if (animFrame < 16) {
            int f = animFrame - 9;
            float fade = 1.0f - f / 7f;

            g.setColor(new Color(0, 0, 0, (int) (100 * fade)));
            g.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i <= f; i++) {
                int ringR = 18 + (f - i) * 22 + i * 12;
                float ringA = fade * (1.0f - i / (float) (f + 1));

                g.setColor(new Color(0, 0, 0, (int) (220 * ringA)));
                g.setStroke(new BasicStroke(3f));
                g.drawOval(cx - ringR, cy - ringR, ringR * 2, ringR * 2);

                int innerR = ringR - 6;
                g.setColor(new Color(255, 40, 40, (int) (170 * ringA)));
                g.setStroke(new BasicStroke(2f));
                g.drawOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);
            }

        } else {
            int f = animFrame - 16;
            float fade = 1.0f - f / 4f;
            g.setColor(new Color(0, 0, 0, (int) (50 * Math.max(0, fade))));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (animFrame >= 6 && animFrame <= 14) {
            float textA = animFrame < 10 ? 1.0f : 1.0f - (animFrame - 10) / 5f;
            g.setColor(new Color(255, 40, 40, (int) (255 * Math.max(0, textA))));
            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            String text = "BLACK FLASH!";
            g.drawString(text, cx - fm.stringWidth(text) / 2, cy - 45);

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
            int radius = 14 + animFrame * 12;
            int alpha = 220 - animFrame * 60;
            g.setColor(new Color(255, 255, 255, Math.max(0, alpha)));
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        } else if (animFrame < 5) {
            int f = animFrame - 2;
            int radius = 20 + f * 14;
            float a = 1.0f - f / 3f;
            g.setColor(new Color(255, 180, 50, (int) (200 * a)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
            g.setStroke(new BasicStroke(1f));
        } else {
            int f = animFrame - 5;
            int radius = 35 + f * 8;
            float a = 1.0f - f / 3f;
            g.setColor(new Color(255, 200, 100, (int) (80 * Math.max(0, a))));
            g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
    }

    private void drawTechniqueEffect(Graphics2D g, int cx, int cy) {
        Stroke old = g.getStroke();
        if (animFrame < 5) {
            
            float a = 1.0f - animFrame / 5f;
            int r = 10 + animFrame * 10;
            g.setColor(new Color(200, 120, 255, (int) (200 * a)));
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
        } else if (animFrame < 12) {
            
            int f = animFrame - 5;
            float fade = 1.0f - f / 7f;
            for (int i = 0; i <= Math.min(f, 2); i++) {
                int ring = 14 + (f - i) * 16;
                float ra = fade * (1.0f - i / 3f);
                g.setColor(new Color(180, 80, 255, (int) (220 * ra)));
                g.setStroke(new BasicStroke(2.5f));
                g.drawOval(cx - ring, cy - ring, ring * 2, ring * 2);
            }
            
            int inner = 6 + f * 4;
            g.setColor(new Color(255, 200, 255, (int) (160 * fade)));
            g.fillOval(cx - inner, cy - inner, inner * 2, inner * 2);
        } else {
            
            int f = animFrame - 12;
            float a = 1.0f - f / 3f;
            int r = 50 + f * 10;
            g.setColor(new Color(150, 60, 220, (int) (60 * Math.max(0, a))));
            g.fillOval(cx - r, cy - r, r * 2, r * 2);
        }
        g.setStroke(old);
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return "" + parts[0].charAt(0) + parts[1].charAt(0);
        }
        return name.substring(0, Math.min(2, name.length()));
    }
}
