package main;

import javax.swing.*;
import java.awt.*;
import controller.GameController;
import ui.*;
import ai.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Chess Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GamePanel gamePanel = new GamePanel();
        ControlPanel controlPanel = new ControlPanel(null);

        // EASY AI (BLACK)
        AI ai = new SimpleAI(GamePanel.BLACK);
        GameController controller = new GameController(gamePanel, controlPanel, ai);

        controlPanel.setController(controller);
        gamePanel.setController(controller);

        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}
