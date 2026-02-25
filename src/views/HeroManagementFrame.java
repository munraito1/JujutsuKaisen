package views;

import controllers.GameManager;
import models.Combatant;
import models.NamedSorcerer;
import models.Sorcerer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HeroManagementFrame extends JDialog {

    private static final Color BG       = new Color(30, 28, 25);
    private static final Color PANEL_BG = new Color(45, 43, 40);
    private static final Color TEXT     = new Color(220, 218, 210);
    private static final Color GREEN    = new Color(40, 140, 40);
    private static final Color GOLD     = new Color(180, 140, 50);
    private static final Color PURPLE   = new Color(100, 60, 180);

    private final GameManager gameManager;
    private JLabel gpLabel;

    public HeroManagementFrame(JFrame parent, GameManager gameManager) {
        super(parent, "Управление командой", true);
        this.gameManager = gameManager;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(6, 6));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(50, 48, 45));
        top.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Управление командой");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(GOLD);
        top.add(title, BorderLayout.WEST);

        gpLabel = new JLabel();
        gpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gpLabel.setForeground(new Color(160, 200, 255));
        top.add(gpLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 8, 0));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        center.add(buildSection("Текущая команда", buildTeamPanel()));
        center.add(buildSection("Скамейка", buildPoolPanel()));
        center.add(buildSection("Найм", buildRecruitPanel()));
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        bottom.setBackground(new Color(40, 38, 35));
        JButton closeBtn = new JButton("Закрыть");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setBackground(new Color(100, 60, 30));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);

        updateGpLabel();
    }

    private JPanel buildSection(String title, JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(PANEL_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 78, 75)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setForeground(GOLD);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildTeamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);

        List<Combatant> members = gameManager.getPlayerTeam().getMembers();
        if (members.isEmpty()) {
            panel.add(emptyLabel("(команда пуста)"));
        }

        for (Combatant c : members) {
            panel.add(buildMemberCard(c));
            panel.add(Box.createVerticalStrut(4));
        }
        panel.add(Box.createVerticalGlue());
        panel.add(new JLabel(String.format("  %d / 5 слотов",
                members.size()), SwingConstants.CENTER));
        return panel;
    }

    private JPanel buildMemberCard(Combatant c) {
        JPanel card = new JPanel(new BorderLayout(4, 0));
        card.setBackground(new Color(50, 80, 50));
        card.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel info = new JLabel(buildHeroHtml(c));
        card.add(info, BorderLayout.CENTER);

        if (c instanceof NamedSorcerer) {
            NamedSorcerer ns = (NamedSorcerer) c;
            
            JButton removeBtn = new JButton("Убрать");
            removeBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            removeBtn.setBackground(new Color(140, 60, 60));
            removeBtn.setForeground(Color.WHITE);
            removeBtn.setFocusPainted(false);
            removeBtn.setOpaque(true);
            removeBtn.addActionListener(e -> {
                gameManager.removeHeroFromTeam(ns);
                refresh();
            });
            card.add(removeBtn, BorderLayout.EAST);
        }
        return card;
    }

    private JPanel buildPoolPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);

        List<NamedSorcerer> pool = gameManager.getHeroPool();
        if (pool.isEmpty()) {
            panel.add(emptyLabel("(нет завербованных)"));
        }

        for (NamedSorcerer hero : pool) {
            panel.add(buildPoolCard(hero));
            panel.add(Box.createVerticalStrut(4));
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildPoolCard(NamedSorcerer hero) {
        JPanel card = new JPanel(new BorderLayout(4, 0));
        card.setBackground(new Color(50, 50, 80));
        card.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        card.add(new JLabel(buildHeroHtml(hero)), BorderLayout.CENTER);

        JButton addBtn = new JButton("В команду");
        addBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        addBtn.setBackground(GREEN);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setOpaque(true);
        addBtn.addActionListener(e -> {
            gameManager.addHeroToTeam(hero);
            refresh();
        });
        card.add(addBtn, BorderLayout.EAST);
        return card;
    }

    private JPanel buildRecruitPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);

        int officeLevel = gameManager.getBuildingManager().getRecruitmentOfficeLevel();

        if (officeLevel == 0) {
            panel.add(emptyLabel("<html><center>Постройте<br>«Приёмную комиссию»</center></html>"));
            panel.add(Box.createVerticalGlue());
            return panel;
        }

        List<NamedSorcerer> available = gameManager.getAvailableForRecruitment();
        if (available.isEmpty()) {
            panel.add(emptyLabel("Все герои завербованы"));
        }

        for (NamedSorcerer hero : available) {
            panel.add(buildRecruitCard(hero, officeLevel));
            panel.add(Box.createVerticalStrut(4));
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildRecruitCard(NamedSorcerer hero, int officeLevel) {
        int cost = gameManager.getRecruitCost(hero);

        JPanel card = new JPanel(new BorderLayout(4, 0));
        card.setBackground(new Color(60, 50, 70));
        card.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        card.add(new JLabel(buildHeroHtml(hero)), BorderLayout.CENTER);

        boolean canAfford = gameManager.getGradePoints() >= cost;
        JButton recruitBtn = new JButton(cost + " ОР");
        recruitBtn.setFont(new Font("Arial", Font.BOLD, 11));
        recruitBtn.setBackground(canAfford ? PURPLE : new Color(80, 78, 75));
        recruitBtn.setForeground(Color.WHITE);
        recruitBtn.setEnabled(canAfford);
        recruitBtn.setFocusPainted(false);
        recruitBtn.setOpaque(true);
        recruitBtn.addActionListener(e -> {
            if (gameManager.recruitHero(hero)) refresh();
        });
        card.add(recruitBtn, BorderLayout.EAST);
        return card;
    }

    private String buildHeroHtml(Combatant c) {
        String grade = "";
        if (c instanceof Sorcerer) grade = " [" + ((Sorcerer) c).getGrade().getDisplayName() + "]";
        return String.format("<html><b>%s</b>%s<br><font color='#aaaaaa'>HP:%d  ATK:%d  Lv.%d</font></html>",
                c.getName(), grade, c.getHp(), c.getAttack(), c.getLevel());
    }

    private JLabel emptyLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(new Color(140, 138, 130));
        lbl.setFont(new Font("Arial", Font.ITALIC, 12));
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        return lbl;
    }

    private void updateGpLabel() {
        gpLabel.setText("ОР: " + gameManager.getGradePoints() + "  ");
    }

    private void refresh() {
        getContentPane().removeAll();
        initUI();
        revalidate();
        repaint();
        pack();
    }
}
