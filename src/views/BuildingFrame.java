package views;

import controllers.BuildingManager;
import controllers.GameManager;
import models.Building;

import javax.swing.*;
import java.awt.*;

public class BuildingFrame extends JDialog {

    private static final Color BG_COLOR        = new Color(50, 48, 45);
    private static final Color PANEL_COLOR     = new Color(60, 58, 55);
    private static final Color TEXT_COLOR      = new Color(220, 220, 210);
    private static final Color LABEL_COLOR     = new Color(160, 160, 150);
    private static final Color BUILT_COLOR     = new Color(80, 180, 80);
    private static final Color NOT_BUILT_COLOR = new Color(120, 120, 120);
    private static final Color BUILD_BTN_COLOR   = new Color(60, 120, 200);
    private static final Color UPGRADE_BTN_COLOR = new Color(180, 130, 30);
    private static final Color MAX_BTN_COLOR     = new Color(80, 80, 80);
    private static final Color YUAN_COLOR      = new Color(220, 180, 50);

    private final GameManager gameManager;
    private JLabel yuanLabel;
    private JPanel buildingsPanel;

    public BuildingFrame(JFrame parent, GameManager gameManager) {
        super(parent, "Здания", true);
        this.gameManager = gameManager;

        setLayout(new BorderLayout(6, 6));
        getContentPane().setBackground(BG_COLOR);
        setResizable(false);

        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel("Здания техникума");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT_COLOR);
        topPanel.add(title, BorderLayout.WEST);

        yuanLabel = new JLabel("¥" + gameManager.getYuan());
        yuanLabel.setFont(new Font("Arial", Font.BOLD, 14));
        yuanLabel.setForeground(YUAN_COLOR);
        topPanel.add(yuanLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        buildingsPanel = new JPanel();
        buildingsPanel.setLayout(new BoxLayout(buildingsPanel, BoxLayout.Y_AXIS));
        buildingsPanel.setBackground(BG_COLOR);
        buildingsPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (Building b : gameManager.getBuildingManager().getAllBuildings()) {
            buildingsPanel.add(createBuildingRow(b));
            buildingsPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(buildingsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_COLOR);
        add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(PANEL_COLOR);
        JButton closeBtn = new JButton("Закрыть");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        closeBtn.setBackground(new Color(90, 88, 85));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createBuildingRow(Building building) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(PANEL_COLOR);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 78, 75), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PANEL_COLOR);
        leftPanel.setPreferredSize(new Dimension(180, 80));

        JLabel nameLabel = new JLabel(building.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_COLOR);

        String levelText = building.isBuilt()
                ? "Уровень " + building.getLevel() + "/" + building.getMaxLevel()
                : "Не построено";
        JLabel levelLabel = new JLabel(levelText);
        levelLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        levelLabel.setForeground(building.isBuilt() ? BUILT_COLOR : NOT_BUILT_COLOR);

        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(levelLabel);
        row.add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(PANEL_COLOR);

        JLabel descLabel = new JLabel(
                "<html><body style='width:220px'>" + building.getDescription() + "</body></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(LABEL_COLOR);

        JLabel effectLabel = new JLabel(building.getEffectDescription());
        effectLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        effectLabel.setForeground(building.isBuilt() ? new Color(140, 200, 140) : LABEL_COLOR);

        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(effectLabel);
        row.add(centerPanel, BorderLayout.CENTER);

        JButton actionBtn = createActionButton(building);
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(PANEL_COLOR);
        rightPanel.add(actionBtn);
        row.add(rightPanel, BorderLayout.EAST);

        return row;
    }

    private JButton createActionButton(Building building) {
        JButton btn;
        BuildingManager bm = gameManager.getBuildingManager();

        if (!building.isBuilt()) {
            btn = new JButton("Построить (" + building.getBaseCost() + "¥)");
            btn.setBackground(BUILD_BTN_COLOR);
            btn.addActionListener(e -> {
                if (bm.build(building, gameManager)) {
                    JOptionPane.showMessageDialog(this,
                            building.getName() + " построено!\n" + building.getEffectDescription(),
                            "Строительство завершено", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Недостаточно иен! Нужно " + building.getBaseCost() + "¥",
                            "Нет денег", JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (building.canUpgrade()) {
            btn = new JButton("Улучшить (" + building.getUpgradeCost() + "¥)");
            btn.setBackground(UPGRADE_BTN_COLOR);
            btn.addActionListener(e -> {
                if (bm.upgrade(building, gameManager)) {
                    JOptionPane.showMessageDialog(this,
                            building.getName() + " улучшено до уровня " + building.getLevel() + "!\n"
                                    + building.getEffectDescription(),
                            "Улучшение завершено", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Недостаточно иен! Нужно " + building.getUpgradeCost() + "¥",
                            "Нет денег", JOptionPane.WARNING_MESSAGE);
                }
            });
        } else {
            btn = new JButton("Макс. уровень");
            btn.setBackground(MAX_BTN_COLOR);
            btn.setEnabled(false);
        }

        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        return btn;
    }

    private void refresh() {
        yuanLabel.setText("¥" + gameManager.getYuan());
        buildingsPanel.removeAll();
        for (Building b : gameManager.getBuildingManager().getAllBuildings()) {
            buildingsPanel.add(createBuildingRow(b));
            buildingsPanel.add(Box.createVerticalStrut(8));
        }
        buildingsPanel.revalidate();
        buildingsPanel.repaint();
    }
}
