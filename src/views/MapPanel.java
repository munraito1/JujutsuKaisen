package views;

import enums.DistrictStatus;
import models.District;
import models.WorldMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class MapPanel extends JPanel {

    private static final int NODE_RADIUS = 30;
    private static final Color BG_TOP          = new Color(30, 28, 25);
    private static final Color BG_BOTTOM       = new Color(45, 43, 40);
    private static final Color EDGE_COLOR      = new Color(90, 88, 84);
    private static final Color EDGE_ACTIVE     = new Color(140, 130, 100);
    private static final Color CONTROLLED_COLOR = new Color(65, 165, 70);
    private static final Color HOSTILE_COLOR    = new Color(195, 55, 55);
    private static final Color LOCKED_COLOR     = new Color(100, 100, 100);
    private static final Color CONTESTED_COLOR  = new Color(210, 185, 40);
    private static final Color PLAYER_MARKER_COLOR = new Color(255, 220, 50);
    private static final Color SELECTED_BORDER_COLOR = new Color(255, 255, 255);
    private static final Color HOVER_BORDER_COLOR    = new Color(200, 200, 200);
    private static final Color TEXT_COLOR      = new Color(220, 218, 210);
    private static final Color INCOME_COLOR    = new Color(180, 230, 120);

    private WorldMap worldMap;
    private District currentDistrict;
    private District selectedDistrict;
    private District hoveredDistrict;

    private DistrictSelectionListener selectionListener;

    public interface DistrictSelectionListener {
        void onDistrictSelected(District district);
    }

    public MapPanel() {
        setBackground(BG_TOP);
        setPreferredSize(new Dimension(800, 520));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                District clicked = findDistrictAt(e.getX(), e.getY());
                if (clicked != null) {
                    selectedDistrict = clicked;
                    if (selectionListener != null) {
                        selectionListener.onDistrictSelected(clicked);
                    }
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                District hovered = findDistrictAt(e.getX(), e.getY());
                if (hovered != hoveredDistrict) {
                    hoveredDistrict = hovered;
                    setCursor(hovered != null
                            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                            : Cursor.getDefaultCursor());
                    repaint();
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
        repaint();
    }

    public void setCurrentDistrict(District district) {
        this.currentDistrict = district;
        repaint();
    }

    public void setSelectedDistrict(District district) {
        this.selectedDistrict = district;
        repaint();
    }

    public District getSelectedDistrict() {
        return selectedDistrict;
    }

    public void setSelectionListener(DistrictSelectionListener listener) {
        this.selectionListener = listener;
    }

    private District findDistrictAt(int mx, int my) {
        if (worldMap == null) return null;
        for (District d : worldMap.getDistricts()) {
            double dist = Math.sqrt(Math.pow(mx - d.getX(), 2) + Math.pow(my - d.getY(), 2));
            if (dist <= NODE_RADIUS + 4) {
                return d;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (worldMap == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GradientPaint bgGradient = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM);
        g2.setPaint(bgGradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        List<District> districts = worldMap.getDistricts();

        for (District d : districts) {
            for (District neighbor : d.getNeighbors()) {
                if (districts.indexOf(d) < districts.indexOf(neighbor)) {
                    drawEdge(g2, d, neighbor);
                }
            }
        }

        for (District d : districts) {
            drawDistrict(g2, d);
        }

        if (currentDistrict != null) {
            drawPlayerMarker(g2, currentDistrict);
        }

        drawLegend(g2);
    }

    private void drawEdge(Graphics2D g2, District a, District b) {
        boolean active = (a == currentDistrict || b == currentDistrict);
        Stroke old = g2.getStroke();
        g2.setStroke(active ? new BasicStroke(2.2f) : new BasicStroke(1.5f));
        g2.setColor(active ? EDGE_ACTIVE : EDGE_COLOR);
        g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        g2.setStroke(old);
    }

    private void drawDistrict(Graphics2D g2, District d) {
        int x = d.getX();
        int y = d.getY();
        int r = NODE_RADIUS;

        Color fillColor = getStatusColor(d.getStatus());

        if (d.getStatus() == DistrictStatus.CONTROLLED) {
            g2.setColor(new Color(80, 220, 80, 35));
            g2.fill(new Ellipse2D.Double(x - r - 6, y - r - 6, (r + 6) * 2, (r + 6) * 2));
        } else if (d.getStatus() == DistrictStatus.HOSTILE) {
            g2.setColor(new Color(220, 60, 60, 30));
            g2.fill(new Ellipse2D.Double(x - r - 5, y - r - 5, (r + 5) * 2, (r + 5) * 2));
        }

        g2.setColor(fillColor);
        g2.fill(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));

        g2.setColor(fillColor.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));

        if (d == selectedDistrict) {
            g2.setColor(SELECTED_BORDER_COLOR);
            g2.setStroke(new BasicStroke(3f));
            g2.draw(new Ellipse2D.Double(x - r - 2, y - r - 2, (r + 2) * 2, (r + 2) * 2));
        } else if (d == hoveredDistrict) {
            g2.setColor(HOVER_BORDER_COLOR);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new Ellipse2D.Double(x - r - 1, y - r - 1, (r + 1) * 2, (r + 1) * 2));
        }

        g2.setStroke(new BasicStroke(1f));

        if (d.getCurseLevel() > 0 && d.getStatus() != DistrictStatus.LOCKED) {
            g2.setColor(new Color(255, 90, 90));
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            String lvl = "Lv." + d.getCurseLevel();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lvl, x - fm.stringWidth(lvl) / 2, y + r + 13);
        }

        if (d.getStatus() == DistrictStatus.CONTROLLED && d.getIncomePerTurn() > 0) {
            g2.setColor(INCOME_COLOR);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String income = "¥" + d.getIncomePerTurn();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(income, x - fm.stringWidth(income) / 2, y + r + 25);
        }

        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String name = d.getName();
        int nameY = (d.getStatus() == DistrictStatus.CONTROLLED && d.getIncomePerTurn() > 0)
                ? y + r + 37 : y + r + 24;
        g2.drawString(name, x - fm.stringWidth(name) / 2, nameY);

        g2.setFont(new Font("Arial", Font.BOLD, 11));
        fm = g2.getFontMetrics();
        String abbrev = d.getStatus().getDisplayName().substring(0, 1);
        g2.setColor(Color.WHITE);
        g2.drawString(abbrev, x - fm.stringWidth(abbrev) / 2, y + fm.getAscent() / 2 - 1);
    }

    private void drawPlayerMarker(Graphics2D g2, District d) {
        int x = d.getX();
        int y = d.getY();
        int size = 9;
        int markerY = y - NODE_RADIUS - 16;

        g2.setColor(new Color(0, 0, 0, 80));
        int[] xs = {x + 1, x + size + 1, x + 1, x - size + 1};
        int[] ys = {markerY - size + 1, markerY + 1, markerY + size + 1, markerY + 1};
        g2.fillPolygon(xs, ys, 4);

        g2.setColor(PLAYER_MARKER_COLOR);
        int[] xPoints = {x, x + size, x, x - size};
        int[] yPoints = {markerY - size, markerY, markerY + size, markerY};
        g2.fillPolygon(xPoints, yPoints, 4);

        g2.setColor(new Color(200, 170, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawPolygon(xPoints, yPoints, 4);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawLegend(Graphics2D g2) {
        int lx = getWidth() - 150;
        int ly = getHeight() - 110;
        int lineH = 18;

        g2.setColor(new Color(20, 18, 16, 180));
        g2.fillRoundRect(lx - 8, ly - 10, 148, 100, 8, 8);
        g2.setColor(new Color(100, 98, 90));
        g2.drawRoundRect(lx - 8, ly - 10, 148, 100, 8, 8);

        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(new Color(180, 178, 170));
        g2.drawString("ЛЕГЕНДА", lx, ly + 2);

        ly += lineH;
        drawLegendItem(g2, lx, ly, CONTROLLED_COLOR, "Контролируется");
        ly += lineH;
        drawLegendItem(g2, lx, ly, HOSTILE_COLOR, "Враждебный");
        ly += lineH;
        drawLegendItem(g2, lx, ly, CONTESTED_COLOR, "Оспаривается");
        ly += lineH;
        drawLegendItem(g2, lx, ly, LOCKED_COLOR, "Заблокирован");
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillOval(x, y - 8, 10, 10);
        g2.setColor(new Color(200, 198, 190));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString(label, x + 14, y);
    }

    private Color getStatusColor(DistrictStatus status) {
        switch (status) {
            case CONTROLLED: return CONTROLLED_COLOR;
            case HOSTILE:    return HOSTILE_COLOR;
            case LOCKED:     return LOCKED_COLOR;
            case CONTESTED:  return CONTESTED_COLOR;
            default:         return LOCKED_COLOR;
        }
    }
}
