package views;

import javax.swing.*;
import java.awt.*;

/**
 * Panel with action buttons for the current player unit's turn.
 */
public class ActionPanel extends JPanel {

    private final JButton moveBtn;
    private final JButton attackBtn;
    private final JButton techniqueBtn;
    private final JButton defendBtn;
    private final JButton endTurnBtn;

    public ActionPanel(BattleFrame parent) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        moveBtn = createButton("Move", new Color(100, 180, 255));
        attackBtn = createButton("Attack", new Color(255, 120, 100));
        techniqueBtn = createButton("Technique", new Color(180, 120, 255));
        defendBtn = createButton("Defend", new Color(100, 200, 130));
        endTurnBtn = createButton("End Turn", new Color(180, 180, 180));

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
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(110, 30));
        return btn;
    }

    public void updateButtons(boolean canMove, boolean canAct, boolean hasAbility, String abilityName) {
        moveBtn.setEnabled(canMove);
        attackBtn.setEnabled(canAct);
        techniqueBtn.setEnabled(canAct && hasAbility);
        if (abilityName != null && !abilityName.isEmpty()) {
            techniqueBtn.setText(abilityName);
        } else {
            techniqueBtn.setText("Technique");
        }
        defendBtn.setEnabled(canAct);
        endTurnBtn.setEnabled(true);
    }

    public JButton getTechniqueButton() { return techniqueBtn; }

    public void disableAll() {
        moveBtn.setEnabled(false);
        attackBtn.setEnabled(false);
        techniqueBtn.setEnabled(false);
        techniqueBtn.setText("Technique");
        defendBtn.setEnabled(false);
        endTurnBtn.setEnabled(false);
    }
}
