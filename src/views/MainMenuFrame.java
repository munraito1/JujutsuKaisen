package views;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    private static final Color BG   = new Color(18, 16, 14);
    private static final Color GOLD = new Color(200, 160, 50);
    private static final Color TEXT = new Color(200, 198, 190);
    private static final Color DIM  = new Color(100, 98, 90);

    public MainMenuFrame() {
        super("Jujutsu Kaisen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BG);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(54, 80, 28, 80));

        JLabel titleLabel = centeredLabel("JUJUTSU KAISEN", 38, Font.BOLD, GOLD);

        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BG);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(16, 110, 46, 110));

        JButton newGame = menuButton("Новая игра", new Color(40, 130, 40));
        JButton exit    = menuButton("Выход",       new Color(130, 40, 40));

        newGame.addActionListener(e -> openTeamSelection());
        exit.addActionListener(e -> System.exit(0));

        btnPanel.add(newGame);
        btnPanel.add(Box.createVerticalStrut(12));
        btnPanel.add(exit);
        add(btnPanel, BorderLayout.CENTER);

    }

    private JLabel centeredLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", style, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        return lbl;
    }

    private JButton menuButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void openTeamSelection() {
        TeamSelectionFrame sel = new TeamSelectionFrame(this);
        sel.setVisible(true);
        setVisible(false);
    }
}
