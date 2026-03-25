package views;

import models.PlayerCharacter;

import javax.swing.*;
import java.awt.*;

public class UnitInfoPanel extends JPanel {

    private final JLabel nameLabel;
    private final JLabel titleLabel;
    private final JLabel levelLabel;
    private final JProgressBar hpBar;
    private final JProgressBar ceBar;
    private final JLabel hpLabel;
    private final JLabel ceLabel;
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

        nameLabel = new JLabel("—");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_MAIN);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        titleLabel = new JLabel(" ");
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        titleLabel.setForeground(TEXT_DIM);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

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

        statsLabel = new JLabel(" ");
        statsLabel.setForeground(TEXT_MAIN);
        statsLabel.setAlignmentX(LEFT_ALIGNMENT);

        rangeLabel = new JLabel(" ");
        rangeLabel.setForeground(TEXT_DIM);
        rangeLabel.setAlignmentX(LEFT_ALIGNMENT);

        add(nameLabel);
        add(titleLabel);
        add(Box.createVerticalStrut(6));
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

    public void showUnit(PlayerCharacter unit) {
        nameLabel.setText(unit.getName());
        titleLabel.setText(unit.getTitle());
        levelLabel.setText("Уровень: " + unit.getLevel());

        hpBar.setMaximum(unit.getMaxHp());
        hpBar.setValue(unit.getHp());
        hpBar.setString(unit.getHp() + " / " + unit.getMaxHp());
        double hpRatio = (double) unit.getHp() / unit.getMaxHp();
        hpBar.setForeground(hpRatio > 0.5 ? new Color(60, 180, 60)
                : hpRatio > 0.25 ? Color.ORANGE : Color.RED);

        ceBar.setMaximum(unit.getMaxCursedEnergy());
        ceBar.setValue(unit.getCursedEnergy());
        ceBar.setString(unit.getCursedEnergy() + " / " + unit.getMaxCursedEnergy());

        statsLabel.setText(String.format(
                "<html>АТК: %d &nbsp; ЗАЩ: %d<br>СКО: %d</html>",
                unit.getAttack(), unit.getDefense(), unit.getSpeed()));
        rangeLabel.setText(String.format("Ход: %d  |  Дальн.: %d",
                unit.getMoveRange(), unit.getAttackRange()));

        revalidate();
        repaint();
    }
}
