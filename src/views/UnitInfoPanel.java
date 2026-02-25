package views;

import models.*;

import javax.swing.*;
import java.awt.*;

public class UnitInfoPanel extends JPanel {

    private final JLabel nameLabel;
    private final JLabel titleLabel;
    private final JLabel gradeLabel;
    private final JLabel levelLabel;
    private final JProgressBar hpBar;
    private final JProgressBar ceBar;
    private final JProgressBar xpBar;
    private final JLabel hpLabel;
    private final JLabel ceLabel;
    private final JLabel xpLabel;
    private final JLabel statsLabel;
    private final JLabel rangeLabel;

    private static final Color PANEL_BG  = new Color(38, 36, 33);
    private static final Color TEXT_MAIN = new Color(220, 218, 210);
    private static final Color TEXT_DIM  = new Color(150, 148, 140);

    public UnitInfoPanel() {
        setPreferredSize(new Dimension(210, 0));
        setBackground(PANEL_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(80, 78, 75)),
                        "Информация",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 11),
                        new Color(160, 158, 150)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        nameLabel = new JLabel("Юнит не выбран");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_MAIN);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        titleLabel = new JLabel(" ");
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        titleLabel.setForeground(TEXT_DIM);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        gradeLabel = new JLabel(" ");
        gradeLabel.setForeground(TEXT_MAIN);
        gradeLabel.setAlignmentX(LEFT_ALIGNMENT);

        levelLabel = new JLabel(" ");
        levelLabel.setForeground(TEXT_MAIN);
        levelLabel.setAlignmentX(LEFT_ALIGNMENT);

        hpLabel = new JLabel("HP:");
        hpLabel.setForeground(TEXT_DIM);
        hpLabel.setAlignmentX(LEFT_ALIGNMENT);
        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setForeground(new Color(60, 180, 60));
        hpBar.setBackground(new Color(55, 53, 50));
        hpBar.setAlignmentX(LEFT_ALIGNMENT);
        hpBar.setMaximumSize(new Dimension(190, 18));

        ceLabel = new JLabel("ПЭ:");
        ceLabel.setForeground(TEXT_DIM);
        ceLabel.setAlignmentX(LEFT_ALIGNMENT);
        ceBar = new JProgressBar(0, 100);
        ceBar.setStringPainted(true);
        ceBar.setForeground(new Color(100, 140, 220));
        ceBar.setBackground(new Color(55, 53, 50));
        ceBar.setAlignmentX(LEFT_ALIGNMENT);
        ceBar.setMaximumSize(new Dimension(190, 18));

        xpLabel = new JLabel("ОП:");
        xpLabel.setForeground(TEXT_DIM);
        xpLabel.setAlignmentX(LEFT_ALIGNMENT);
        xpBar = new JProgressBar(0, 100);
        xpBar.setStringPainted(true);
        xpBar.setForeground(new Color(180, 140, 40));
        xpBar.setBackground(new Color(55, 53, 50));
        xpBar.setAlignmentX(LEFT_ALIGNMENT);
        xpBar.setMaximumSize(new Dimension(190, 14));
        xpLabel.setVisible(false);
        xpBar.setVisible(false);

        statsLabel = new JLabel(" ");
        statsLabel.setForeground(TEXT_MAIN);
        statsLabel.setAlignmentX(LEFT_ALIGNMENT);

        rangeLabel = new JLabel(" ");
        rangeLabel.setForeground(TEXT_DIM);
        rangeLabel.setAlignmentX(LEFT_ALIGNMENT);

        add(nameLabel);
        add(titleLabel);
        add(Box.createVerticalStrut(6));
        add(gradeLabel);
        add(levelLabel);
        add(Box.createVerticalStrut(8));
        add(hpLabel);
        add(hpBar);
        add(Box.createVerticalStrut(4));
        add(ceLabel);
        add(ceBar);
        add(Box.createVerticalStrut(4));
        add(xpLabel);
        add(xpBar);
        add(Box.createVerticalStrut(8));
        add(statsLabel);
        add(rangeLabel);
        add(Box.createVerticalGlue());
    }

    public void showUnit(Combatant unit) {
        nameLabel.setText(unit.getName());
        levelLabel.setText("Уровень: " + unit.getLevel());
        statsLabel.setText(String.format("<html>АТК: %d &nbsp; ЗАЩ: %d<br>СКО: %d</html>",
                unit.getAttack(), unit.getDefense(), unit.getSpeed()));
        rangeLabel.setText(String.format("Ход: %d  |  Дальн.: %d",
                unit.getMovementRange(), unit.getAttackRange()));

        hpBar.setMaximum(unit.getMaxHp());
        hpBar.setValue(unit.getHp());
        hpBar.setString(unit.getHp() + " / " + unit.getMaxHp());
        hpLabel.setText("HP:");

        double hpRatio = (double) unit.getHp() / unit.getMaxHp();
        hpBar.setForeground(hpRatio > 0.5 ? new Color(60, 180, 60)
                : hpRatio > 0.25 ? Color.ORANGE : Color.RED);

        xpBar.setMaximum(unit.getExpToNextLevel());
        xpBar.setValue(unit.getExperience());
        xpBar.setString(unit.getExperience() + " / " + unit.getExpToNextLevel() + " ОП");
        xpLabel.setVisible(true);
        xpBar.setVisible(true);

        if (unit instanceof NamedSorcerer) {
            NamedSorcerer ns = (NamedSorcerer) unit;
            titleLabel.setText(ns.getTitle());
            gradeLabel.setText("Ранг: " + ns.getGrade().getDisplayName());
            ceBar.setMaximum(ns.getMaxCursedEnergy());
            ceBar.setValue(ns.getCursedEnergy());
            ceBar.setString(ns.getCursedEnergy() + " / " + ns.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else if (unit instanceof Sorcerer) {
            Sorcerer s = (Sorcerer) unit;
            titleLabel.setText("Отряд");
            gradeLabel.setText("Ранг: " + s.getGrade().getDisplayName());
            ceBar.setMaximum(s.getMaxCursedEnergy());
            ceBar.setValue(s.getCursedEnergy());
            ceBar.setString(s.getCursedEnergy() + " / " + s.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else if (unit instanceof SpecialCurse) {
            SpecialCurse sc = (SpecialCurse) unit;
            titleLabel.setText("Домен: " + sc.getDomainName());
            gradeLabel.setText("Ранг: " + sc.getCurseGrade().getDisplayName());
            ceBar.setMaximum(sc.getMaxCursedEnergy());
            ceBar.setValue(sc.getCursedEnergy());
            ceBar.setString(sc.getCursedEnergy() + " / " + sc.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else if (unit instanceof CursedSpirit) {
            CursedSpirit cs = (CursedSpirit) unit;
            titleLabel.setText("Проклятый дух");
            gradeLabel.setText("Ранг: " + cs.getCurseGrade().getDisplayName());
            ceBar.setMaximum(cs.getMaxCursedEnergy());
            ceBar.setValue(cs.getCursedEnergy());
            ceBar.setString(cs.getCursedEnergy() + " / " + cs.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else {
            titleLabel.setText(" ");
            gradeLabel.setText(" ");
            ceBar.setVisible(false);
            ceLabel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    public void clear() {
        nameLabel.setText("Юнит не выбран");
        titleLabel.setText(" ");
        gradeLabel.setText(" ");
        levelLabel.setText(" ");
        hpBar.setValue(0);
        hpBar.setString("");
        ceBar.setValue(0);
        ceBar.setString("");
        ceBar.setVisible(false);
        ceLabel.setVisible(false);
        xpBar.setValue(0);
        xpBar.setString("");
        xpLabel.setVisible(false);
        xpBar.setVisible(false);
        statsLabel.setText(" ");
        rangeLabel.setText(" ");
    }
}
