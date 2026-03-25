import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MapGame extends JFrame {

    private static final int COLS = 15;
    private static final int ROWS = 12;
    private static final int TILE = 52;

    private int charX = 0;
    private int charY = 0;

    private final GridPanel grid;

    public MapGame() {
        super("JujutsuKaisen — Map");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        grid = new GridPanel();
        add(grid);
        pack();
        setLocationRelativeTo(null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP,    KeyEvent.VK_W -> moveChar(0, -1);
                    case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> moveChar(0,  1);
                    case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> moveChar(-1, 0);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> moveChar( 1, 0);
                }
            }
        });
        setFocusable(true);
    }

    private void moveChar(int dx, int dy) {
        int nx = charX + dx;
        int ny = charY + dy;
        if (nx >= 0 && nx < COLS && ny >= 0 && ny < ROWS) {
            charX = nx;
            charY = ny;
            grid.repaint();
        }
    }

    private class GridPanel extends JPanel {

        private static final Color TILE_LIGHT  = new Color(55, 53, 50);
        private static final Color TILE_DARK   = new Color(44, 42, 39);
        private static final Color TILE_BORDER = new Color(30, 28, 26);
        private static final Color CHAR_FILL   = new Color(60, 130, 230);
        private static final Color CHAR_BORDER = new Color(30, 80, 160);
        private static final Color GLOW        = new Color(255, 220, 50, 150);

        GridPanel() {
            setPreferredSize(new Dimension(COLS * TILE, ROWS * TILE + 28));
            setBackground(new Color(20, 18, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Grid tiles
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    int px = x * TILE;
                    int py = y * TILE;
                    g2.setColor((x + y) % 2 == 0 ? TILE_LIGHT : TILE_DARK);
                    g2.fillRect(px, py, TILE, TILE);
                    g2.setColor(TILE_BORDER);
                    g2.drawRect(px, py, TILE, TILE);
                }
            }

            // Character glow
            int cx = charX * TILE;
            int cy = charY * TILE;
            int margin = 6;
            int size = TILE - margin * 2;
            g2.setColor(GLOW);
            g2.fillOval(cx + margin - 6, cy + margin - 6, size + 12, size + 12);

            // Character circle
            g2.setColor(CHAR_FILL);
            g2.fillOval(cx + margin, cy + margin, size, size);
            g2.setColor(CHAR_BORDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx + margin, cy + margin, size, size);

            // Character label
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String label = "YI";
            g2.drawString(label,
                    cx + (TILE - fm.stringWidth(label)) / 2,
                    cy + TILE / 2 + fm.getAscent() / 2 - 4);

            // Status bar
            int barY = ROWS * TILE;
            g2.setColor(new Color(30, 28, 26));
            g2.fillRect(0, barY, COLS * TILE, 28);
            g2.setColor(new Color(180, 160, 100));
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(
                    String.format("Yuji Itadori   Позиция: (%d, %d)   [WASD / стрелки — движение]", charX, charY),
                    10, barY + 18);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapGame().setVisible(true));
    }
}
