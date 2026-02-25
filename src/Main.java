import views.MainMenuFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame menu = new MainMenuFrame();
            menu.setVisible(true);
        });
    }
}
