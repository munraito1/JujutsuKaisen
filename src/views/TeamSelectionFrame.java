package views;

import enums.Grade;
import models.Combatant;
import models.Sorcerer;
import models.SorcererTeam;
import models.heroes.MegumiFushiguro;
import models.heroes.NobaraKugisaki;
import models.heroes.YujiItadori;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectionFrame extends JFrame {

    private static final Color BG       = new Color(22, 20, 18);
    private static final Color PANEL_BG = new Color(38, 36, 33);
    private static final Color GOLD     = new Color(200, 160, 50);
    private static final Color DIM      = new Color(110, 108, 100);

    private final JFrame parent;
    private final Map<Combatant, JCheckBox> heroChecks = new LinkedHashMap<>();

    public TeamSelectionFrame(JFrame parent) {
        super("Jujutsu Kaisen — Выбор команды");
        this.parent = parent;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 28, 25));
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("Выберите стартовую команду (1–3 героя)");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(GOLD);
        header.add(title, BorderLayout.WEST);

        JLabel hint = new JLabel("Нажмите на карточку для переключения");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(DIM);
        header.add(hint, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel heroPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        heroPanel.setBackground(BG);
        heroPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        List<Combatant> heroes = List.of(
                new YujiItadori(), new MegumiFushiguro(), new NobaraKugisaki());
        for (Combatant hero : heroes) {
            JCheckBox check = new JCheckBox();
            check.setSelected(true);
            check.setOpaque(false);
            heroChecks.put(hero, check);
            heroPanel.add(buildHeroCard(hero, check));
        }
        add(heroPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
        footer.setBackground(new Color(28, 26, 23));

        JButton backBtn  = styledButton("◀  Назад",        new Color(80, 78, 75));
        JButton startBtn = styledButton("Начать игру  ▶",  new Color(20, 87, 20));

        backBtn.addActionListener(e -> { parent.setVisible(true); dispose(); });
        startBtn.addActionListener(e -> startGame());

        footer.add(backBtn);
        footer.add(startBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildHeroCard(Combatant hero, JCheckBox check) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 78, 75), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        card.setPreferredSize(new Dimension(210, 210));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel nameLabel = new JLabel(hero.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(GOLD);
        topRow.add(nameLabel, BorderLayout.WEST);
        topRow.add(check, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        String gradeStr = "";
        if (hero instanceof Sorcerer s) {
            Grade g = s.getGrade();
            gradeStr = g != null ? g.getDisplayName() : "";
        }
        String stats = String.format(
                "<html><font color='#b0b0a0'>" +
                "Класс: %s<br>" +
                "HP: %d &nbsp;&nbsp; ATK: %d<br>" +
                "DEF: %d &nbsp; SPD: %d<br>" +
                "Уровень: %d" +
                "</font></html>",
                gradeStr, hero.getMaxHp(), hero.getAttack(),
                hero.getDefense(), hero.getSpeed(), hero.getLevel());

        JLabel statsLabel = new JLabel(stats);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(statsLabel, BorderLayout.CENTER);

        JLabel roleLabel = new JLabel(heroRole(hero.getName()), SwingConstants.CENTER);
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        roleLabel.setForeground(DIM);
        card.add(roleLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() != check) check.setSelected(!check.isSelected());
            }
        });

        return card;
    }

    private String heroRole(String name) {
        return switch (name) {
            case "Yuji Itadori"    -> "Рукопашный боец";
            case "Megumi Fushiguro" -> "Призыватель shikigami";
            case "Nobara Kugisaki"  -> "Дистанционный урон";
            default -> "";
        };
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void startGame() {
        List<Combatant> selected = new ArrayList<>();
        for (Map.Entry<Combatant, JCheckBox> entry : heroChecks.entrySet()) {
            if (entry.getValue().isSelected()) selected.add(entry.getKey());
        }

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Выберите хотя бы одного героя!", "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        SorcererTeam playerTeam = new SorcererTeam("Tokyo Jujutsu High");
        for (Combatant hero : selected) playerTeam.addMember(hero);

        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame(playerTeam);
            gameFrame.setVisible(true);
            dispose();
        });
    }
}
