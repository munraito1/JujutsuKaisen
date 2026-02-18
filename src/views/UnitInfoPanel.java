package views;

import models.*;

import javax.swing.*;
import java.awt.*;

/**
 * Panel showing detailed info about a selected unit.
 */
public class UnitInfoPanel extends JPanel {

    private final JLabel nameLabel;
    private final JLabel titleLabel;
    private final JLabel gradeLabel;
    private final JLabel levelLabel;
    private final JProgressBar hpBar;
    private final JProgressBar ceBar;
    private final JLabel hpLabel;
    private final JLabel ceLabel;
    private final JLabel statsLabel;
    private final JLabel rangeLabel;

    public UnitInfoPanel() {
        setPreferredSize(new Dimension(210, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Unit Info"),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        nameLabel = new JLabel("No unit selected");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        titleLabel = new JLabel(" ");
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        gradeLabel = new JLabel(" ");
        gradeLabel.setAlignmentX(LEFT_ALIGNMENT);

        levelLabel = new JLabel(" ");
        levelLabel.setAlignmentX(LEFT_ALIGNMENT);

        hpLabel = new JLabel("HP:");
        hpLabel.setAlignmentX(LEFT_ALIGNMENT);
        hpBar = new JProgressBar(0, 100);
        hpBar.setStringPainted(true);
        hpBar.setForeground(new Color(60, 180, 60));
        hpBar.setAlignmentX(LEFT_ALIGNMENT);
        hpBar.setMaximumSize(new Dimension(190, 18));

        ceLabel = new JLabel("CE:");
        ceLabel.setAlignmentX(LEFT_ALIGNMENT);
        ceBar = new JProgressBar(0, 100);
        ceBar.setStringPainted(true);
        ceBar.setForeground(new Color(100, 140, 220));
        ceBar.setAlignmentX(LEFT_ALIGNMENT);
        ceBar.setMaximumSize(new Dimension(190, 18));

        statsLabel = new JLabel(" ");
        statsLabel.setAlignmentX(LEFT_ALIGNMENT);

        rangeLabel = new JLabel(" ");
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
        add(Box.createVerticalStrut(8));
        add(statsLabel);
        add(rangeLabel);
        add(Box.createVerticalGlue());
    }

    public void showUnit(Combatant unit) {
        nameLabel.setText(unit.getName());
        levelLabel.setText("Level: " + unit.getLevel());
        statsLabel.setText(String.format("<html>ATK: %d &nbsp; DEF: %d<br>SPD: %d</html>",
                unit.getAttack(), unit.getDefense(), unit.getSpeed()));
        rangeLabel.setText(String.format("Move: %d  |  Range: %d",
                unit.getMovementRange(), unit.getAttackRange()));

        hpBar.setMaximum(unit.getMaxHp());
        hpBar.setValue(unit.getHp());
        hpBar.setString(unit.getHp() + " / " + unit.getMaxHp());
        hpLabel.setText("HP:");

        double hpRatio = (double) unit.getHp() / unit.getMaxHp();
        hpBar.setForeground(hpRatio > 0.5 ? new Color(60, 180, 60)
                : hpRatio > 0.25 ? Color.ORANGE : Color.RED);

        if (unit instanceof NamedSorcerer) {
            NamedSorcerer ns = (NamedSorcerer) unit;
            titleLabel.setText(ns.getTitle());
            gradeLabel.setText("Grade: " + ns.getGrade().getDisplayName());
            ceBar.setMaximum(ns.getMaxCursedEnergy());
            ceBar.setValue(ns.getCursedEnergy());
            ceBar.setString(ns.getCursedEnergy() + " / " + ns.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else if (unit instanceof Sorcerer) {
            Sorcerer s = (Sorcerer) unit;
            titleLabel.setText("Squad");
            gradeLabel.setText("Grade: " + s.getGrade().getDisplayName());
            ceBar.setMaximum(s.getMaxCursedEnergy());
            ceBar.setValue(s.getCursedEnergy());
            ceBar.setString(s.getCursedEnergy() + " / " + s.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else if (unit instanceof SpecialCurse) {
            SpecialCurse sc = (SpecialCurse) unit;
            titleLabel.setText("Domain: " + sc.getDomainName());
            gradeLabel.setText("Grade: " + sc.getCurseGrade().getDisplayName());
            ceBar.setMaximum(sc.getMaxCursedEnergy());
            ceBar.setValue(sc.getCursedEnergy());
            ceBar.setString(sc.getCursedEnergy() + " / " + sc.getMaxCursedEnergy());
            ceBar.setVisible(true);
            ceLabel.setVisible(true);
        } else if (unit instanceof CursedSpirit) {
            CursedSpirit cs = (CursedSpirit) unit;
            titleLabel.setText("Cursed Spirit");
            gradeLabel.setText("Grade: " + cs.getCurseGrade().getDisplayName());
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
        nameLabel.setText("No unit selected");
        titleLabel.setText(" ");
        gradeLabel.setText(" ");
        levelLabel.setText(" ");
        hpBar.setValue(0);
        hpBar.setString("");
        ceBar.setValue(0);
        ceBar.setString("");
        ceBar.setVisible(false);
        ceLabel.setVisible(false);
        statsLabel.setText(" ");
        rangeLabel.setText(" ");
    }
}
