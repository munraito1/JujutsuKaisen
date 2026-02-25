package views;

import controllers.GameManager;
import models.TechTree;
import models.Technology;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TechTreeFrame extends JDialog {

    private static final Color BG         = new Color(30, 28, 25);
    private static final Color PATH_BG    = new Color(45, 43, 40);
    private static final Color RESEARCHED = new Color(40, 100, 40);
    private static final Color AVAILABLE  = new Color(80, 60, 140);
    private static final Color LOCKED     = new Color(70, 68, 65);
    private static final Color TEXT_COLOR = new Color(220, 218, 210);

    private final GameManager gameManager;
    private final TechTree techTree;
    private JLabel gpLabel;

    private static final String[] PATH_ORDER = {"COMBAT", "CE", "BARRIERS", "MEDICINE"};
    private static final Map<String, String> PATH_NAMES = new LinkedHashMap<>();
    static {
        PATH_NAMES.put("COMBAT",   "Боевые техники");
        PATH_NAMES.put("CE",       "Проклятая энергия");
        PATH_NAMES.put("BARRIERS", "Барьеры");
        PATH_NAMES.put("MEDICINE", "Медицина");
    }

    public TechTreeFrame(JFrame parent, GameManager gameManager) {
        super(parent, "Древо технологий", true);
        this.gameManager = gameManager;
        this.techTree    = gameManager.getTechTree();

        initUI();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(8, 8));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 48, 45));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Древо технологий");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(200, 170, 80));
        topPanel.add(title, BorderLayout.WEST);

        gpLabel = new JLabel();
        gpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gpLabel.setForeground(new Color(160, 200, 255));
        topPanel.add(gpLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel pathsPanel = new JPanel(new GridLayout(1, PATH_ORDER.length, 10, 0));
        pathsPanel.setBackground(BG);
        pathsPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        for (String path : PATH_ORDER) {
            pathsPanel.add(buildPathColumn(path));
        }
        add(pathsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 4));
        bottomPanel.setBackground(new Color(40, 38, 35));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 8, 12));

        JTextArea effectsArea = buildEffectsArea();
        bottomPanel.add(new JScrollPane(effectsArea), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Закрыть");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setBackground(new Color(100, 60, 30));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.addActionListener(e -> dispose());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setBackground(new Color(40, 38, 35));
        btnWrap.add(closeBtn);
        bottomPanel.add(btnWrap, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        updateGpLabel();
    }

    private JPanel buildPathColumn(String path) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(PATH_BG);
        col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 78, 75), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel header = new JLabel(PATH_NAMES.get(path), SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setForeground(new Color(200, 170, 80));
        header.setAlignmentX(CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        col.add(header);
        col.add(Box.createVerticalStrut(10));

        List<Technology> techs = new ArrayList<>();
        for (Technology t : Technology.values()) {
            if (t.getPath().equals(path)) techs.add(t);
        }

        for (int i = 0; i < techs.size(); i++) {
            Technology tech = techs.get(i);
            col.add(buildTechCard(tech));
            if (i < techs.size() - 1) {
                col.add(buildArrow());
            }
        }

        col.add(Box.createVerticalGlue());
        return col;
    }

    private JPanel buildTechCard(Technology tech) {
        boolean researched  = techTree.isResearched(tech);
        boolean canResearch = techTree.canResearch(tech);

        Color bg = researched ? RESEARCHED : (canResearch ? AVAILABLE : LOCKED);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("<html><b>" + tech.getDisplayName() + "</b></html>");
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(3));

        JLabel descLabel = new JLabel("<html><span style='font-size:10px'>"
                + tech.getDescription() + "</span></html>");
        descLabel.setForeground(new Color(180, 178, 170));
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(descLabel);
        card.add(Box.createVerticalStrut(4));

        if (researched) {
            JLabel doneLabel = new JLabel("✓ Изучено");
            doneLabel.setForeground(new Color(120, 220, 120));
            doneLabel.setFont(new Font("Arial", Font.BOLD, 11));
            doneLabel.setAlignmentX(LEFT_ALIGNMENT);
            card.add(doneLabel);
        } else if (canResearch) {
            String costText = tech.getCost() == 0 ? "Бесплатно" : tech.getCost() + " ОР";
            JButton researchBtn = new JButton("Изучить (" + costText + ")");
            researchBtn.setFont(new Font("Arial", Font.BOLD, 11));
            researchBtn.setBackground(new Color(120, 80, 200));
            researchBtn.setForeground(Color.WHITE);
            researchBtn.setFocusPainted(false);
            researchBtn.setOpaque(true);
            researchBtn.setAlignmentX(LEFT_ALIGNMENT);
            researchBtn.addActionListener(e -> onResearch(tech));
            card.add(researchBtn);
        } else {
            JLabel lockedLabel = new JLabel("Заблокировано");
            lockedLabel.setForeground(new Color(140, 138, 130));
            lockedLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            lockedLabel.setAlignmentX(LEFT_ALIGNMENT);
            card.add(lockedLabel);
        }

        return card;
    }

    private JLabel buildArrow() {
        JLabel arrow = new JLabel("↓", SwingConstants.CENTER);
        arrow.setForeground(new Color(150, 148, 140));
        arrow.setFont(new Font("Arial", Font.BOLD, 18));
        arrow.setAlignmentX(CENTER_ALIGNMENT);
        arrow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return arrow;
    }

    private JTextArea buildEffectsArea() {
        StringBuilder sb = new StringBuilder("Активные бонусы:  ");
        double dmg = techTree.getDamageBonusPct();
        if (dmg > 0) sb.append(String.format("+%.0f%% урон  ", dmg * 100));
        double ceMult = techTree.getCECostMultiplier();
        if (ceMult < 1.0) sb.append(String.format("-%d%% ПЭ стоимость  ", (int)((1 - ceMult) * 100)));
        int bonusCE = techTree.getBonusMaxCE();
        if (bonusCE > 0) sb.append(String.format("+%d макс.ПЭ  ", bonusCE));
        double spawn = techTree.getSpawnChanceMultiplier();
        if (spawn < 1.0) sb.append(String.format("-%d%% спавн  ", (int)((1 - spawn) * 100)));
        double heal = techTree.getFieldHealBonus();
        if (heal > 0) sb.append(String.format("+%.0f%% лечение в поле  ", heal * 100));
        if (sb.toString().equals("Активные бонусы:  ")) sb.append("нет");

        JTextArea area = new JTextArea(sb.toString().trim());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(40, 38, 35));
        area.setForeground(new Color(160, 200, 120));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setPreferredSize(new Dimension(0, 36));
        return area;
    }

    private void onResearch(Technology tech) {
        boolean ok = techTree.research(tech, gameManager.getResourceManager());
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "«" + tech.getDisplayName() + "» успешно изучено!",
                    "Технология изучена", JOptionPane.INFORMATION_MESSAGE);
            
            getContentPane().removeAll();
            initUI();
            revalidate();
            repaint();
            pack();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Недостаточно Очков Ранга (ОР) или требования не выполнены.",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateGpLabel() {
        gpLabel.setText("ОР: " + gameManager.getGradePoints() + "  ");
    }
}
