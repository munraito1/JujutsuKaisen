package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActionPanel extends JPanel {

    private final JButton moveBtn;
    private final JButton attackBtn;
    private final JButton techniqueBtn;
    private final JButton defendBtn;
    private final JButton endTurnBtn;

    private static final Color MOVE_COLOR    = new Color(60, 140, 220);
    private static final Color ATTACK_COLOR  = new Color(220, 80, 70);
    private static final Color TECH_COLOR    = new Color(150, 90, 230);
    private static final Color DEFEND_COLOR  = new Color(70, 180, 110);
    private static final Color END_COLOR     = new Color(140, 140, 140);

    public ActionPanel(BattleFrame parent) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        setBackground(new Color(42, 40, 37));

        moveBtn      = createButton("Движение",   MOVE_COLOR);
        attackBtn    = createButton("Атака",       ATTACK_COLOR);
        techniqueBtn = createButton("Техника",     TECH_COLOR);
        defendBtn    = createButton("Защита",      DEFEND_COLOR);
        endTurnBtn   = createButton("Конец хода",  END_COLOR);

        moveBtn.addActionListener(e -> parent.onMoveClicked());
        attackBtn.addActionListener(e -> parent.onAttackClicked());
        techniqueBtn.addActionListener(e -> parent.onTechniqueClicked());
        defendBtn.addActionListener(e -> parent.onDefendClicked());
        endTurnBtn.addActionListener(e -> parent.onEndTurnClicked());

        add(moveBtn);
        add(attackBtn);
        add(techniqueBtn);
        add(defendBtn);
        add(endTurnBtn);

        disableAll();
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(118, 32));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bg.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    public void updateButtons(boolean canMove, boolean canAct, boolean hasAbility, String abilityName) {
        moveBtn.setEnabled(canMove);
        attackBtn.setEnabled(canAct);
        techniqueBtn.setEnabled(canAct && hasAbility);
        if (abilityName != null && !abilityName.isEmpty()) {
            techniqueBtn.setText(abilityName);
        } else {
            techniqueBtn.setText("Техника");
        }
        defendBtn.setEnabled(canAct);
        endTurnBtn.setEnabled(true);
    }

    public JButton getTechniqueButton() { return techniqueBtn; }

    public void disableAll() {
        moveBtn.setEnabled(false);
        attackBtn.setEnabled(false);
        techniqueBtn.setEnabled(false);
        techniqueBtn.setText("Техника");
        defendBtn.setEnabled(false);
        endTurnBtn.setEnabled(false);
    }
}
