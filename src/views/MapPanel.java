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

/**
 * Renders the world map as a node graph.
 * Districts are circles, connections are lines.
 * Clicking a district selects it.
 */
public class MapPanel extends JPanel {

    private static final int NODE_RADIUS = 30;
    private static final Color BG_COLOR = new Color(50, 48, 45);
    private static final Color EDGE_COLOR = new Color(100, 100, 100);
    private static final Color CONTROLLED_COLOR = new Color(80, 180, 80);
    private static final Color HOSTILE_COLOR = new Color(200, 60, 60);
    private static final Color LOCKED_COLOR = new Color(120, 120, 120);
    private static final Color CONTESTED_COLOR = new Color(220, 200, 50);
    private static final Color PLAYER_MARKER_COLOR = new Color(255, 220, 50);
    private static final Color SELECTED_BORDER_COLOR = new Color(255, 255, 255);
    private static final Color HOVER_BORDER_COLOR = new Color(180, 180, 180);
    private static final Color TEXT_COLOR = new Color(220, 220, 210);

    private WorldMap worldMap;
    private District currentDistrict;
    private District selectedDistrict;
    private District hoveredDistrict;

    private DistrictSelectionListener selectionListener;

    public interface DistrictSelectionListener {
        void onDistrictSelected(District district);
    }

    public MapPanel() {
        setBackground(BG_COLOR);
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
            if (dist <= NODE_RADIUS) {
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

        List<District> districts = worldMap.getDistricts();

        // Draw edges first (behind nodes)
        g2.setStroke(new BasicStroke(2));
        g2.setColor(EDGE_COLOR);
        for (District d : districts) {
            for (District neighbor : d.getNeighbors()) {
                // Draw each edge only once
                if (districts.indexOf(d) < districts.indexOf(neighbor)) {
                    g2.drawLine(d.getX(), d.getY(), neighbor.getX(), neighbor.getY());
                }
            }
        }

        // Draw nodes
        for (District d : districts) {
            drawDistrict(g2, d);
        }

        // Draw player marker on current district
        if (currentDistrict != null) {
            drawPlayerMarker(g2, currentDistrict);
        }
    }

    private void drawDistrict(Graphics2D g2, District d) {
        int x = d.getX();
        int y = d.getY();
        int r = NODE_RADIUS;

        // Fill color based on status
        Color fillColor = getStatusColor(d.getStatus());

        // Draw filled circle
        g2.setColor(fillColor);
        g2.fill(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));

        // Border: white if selected, lighter gray if hovered
        if (d == selectedDistrict) {
            g2.setColor(SELECTED_BORDER_COLOR);
            g2.setStroke(new BasicStroke(3));
            g2.draw(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));
        } else if (d == hoveredDistrict) {
            g2.setColor(HOVER_BORDER_COLOR);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));
        }

        // Curse level indicator (small dots)
        if (d.getCurseLevel() > 0 && d.getStatus() != DistrictStatus.LOCKED) {
            g2.setColor(new Color(255, 80, 80));
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            String lvl = "Lv." + d.getCurseLevel();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lvl, x - fm.stringWidth(lvl) / 2, y + r + 12);
        }

        // District name below the node
        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        String name = d.getName();
        g2.drawString(name, x - fm.stringWidth(name) / 2, y + r + 24);

        // Status icon inside circle
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        String abbrev = d.getStatus().getDisplayName().substring(0, 1);
        g2.setColor(Color.WHITE);
        fm = g2.getFontMetrics();
        g2.drawString(abbrev, x - fm.stringWidth(abbrev) / 2, y + fm.getAscent() / 2 - 1);
    }

    private void drawPlayerMarker(Graphics2D g2, District d) {
        int x = d.getX();
        int y = d.getY();
        int size = 10;

        // Golden diamond marker above the district node
        int markerY = y - NODE_RADIUS - 14;

        g2.setColor(PLAYER_MARKER_COLOR);
        int[] xPoints = {x, x + size, x, x - size};
        int[] yPoints = {markerY - size, markerY, markerY + size, markerY};
        g2.fillPolygon(xPoints, yPoints, 4);

        g2.setColor(new Color(200, 170, 0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawPolygon(xPoints, yPoints, 4);
    }

    private Color getStatusColor(DistrictStatus status) {
        switch (status) {
            case CONTROLLED: return CONTROLLED_COLOR;
            case HOSTILE: return HOSTILE_COLOR;
            case LOCKED: return LOCKED_COLOR;
            case CONTESTED: return CONTESTED_COLOR;
            default: return LOCKED_COLOR;
        }
    }
}
