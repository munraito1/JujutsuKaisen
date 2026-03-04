package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Общие вспомогательные методы для Swing-компонентов. */
public final class UIUtils {

    private UIUtils() {}

    /**
     * Создаёт стилизованную кнопку с тёмной темой, hover-эффектом и курсором-рукой.
     */
    public static JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(bg.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }
}
