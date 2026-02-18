import models.*;
import models.heroes.*;
import views.GameFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Player team
            SorcererTeam playerTeam = new SorcererTeam("Tokyo Jujutsu High");
            playerTeam.addMember(new YujiItadori());
            playerTeam.addMember(new MegumiFushiguro());
            playerTeam.addMember(new NobaraKugisaki());

            GameFrame frame = new GameFrame(playerTeam);
            frame.setVisible(true);
        });
    }
}
