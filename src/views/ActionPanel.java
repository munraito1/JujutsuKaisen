package views;

import javax.swing.*;
import java.awt.*;

public class ActionPanel extends JPanel {

    private final JButton moveBtn;
    private final JButton attackBtn;
    private final JButton techniqueBtn;
    private final JButton defendBtn;
    private final JButton endTurnBtn;

    private static final Color MOVE_COLOR   = new Color(60, 140, 220);
    private static final Color ATTACK_COLOR = new Color(220, 80, 70);
    private static final Color TECH_COLOR   = new Color(150, 90, 230);
    private static final Color DEFEND_COLOR = new Color(70, 180, 110);
    private static final Color END_COLOR    = new Color(140, 140, 140);

    public ActionPanel(BattleFrame parent) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        setBackground(new Color(42, 40, 37));

        moveBtn      = createBtn("Движение",  MOVE_COLOR);
        attackBtn    = createBtn("Атака",      ATTACK_COLOR);
        techniqueBtn = createBtn("Техника",    TECH_COLOR);
        defendBtn    = createBtn("Защита",     DEFEND_COLOR);
        endTurnBtn   = createBtn("Конец хода", END_COLOR);

        moveBtn.addActionListener(e -> parent.onMoveClicked());

        add(moveBtn);
        add(attackBtn);
        add(techniqueBtn);
        add(defendBtn);
        add(endTurnBtn);

        // только "Движение" доступно
        attackBtn.setEnabled(false);
        techniqueBtn.setEnabled(false);
        defendBtn.setEnabled(false);
        endTurnBtn.setEnabled(false);
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = UIUtils.createStyledButton(text, bg);
        btn.setPreferredSize(new Dimension(118, 32));
        return btn;
    }

    public void setMoveEnabled(boolean enabled) {
        moveBtn.setEnabled(enabled);
    }
}
