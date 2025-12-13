
package ui;

import javax.swing.*;

import ai.SimpleAI;
import ai.AlphaBetaAI;
import ai.MinimaxAI;

import java.awt.*;
import controller.GameController;

public class ControlPanel extends JPanel {
	private JLabel turnLabel;
	private JLabel winnerLabel;
	private JButton newGameButton;
	private JButton exitButton;
	private JComboBox<String> aiLevelCombo;
	private JTextArea moveLogArea;
	private GameController controller;
	
	public ControlPanel(GameController gc) {
		this.controller = gc;
		setPreferredSize(new Dimension(300, 1000)); // sidebar rộng 300px
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// --- MOVE LOG ---
		moveLogArea = new JTextArea();
		moveLogArea.setEditable(false);
		moveLogArea.setLineWrap(true);
		moveLogArea.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(moveLogArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);

		// --- BUTTONS ---
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(4, 1, 10, 10));

		newGameButton = new JButton("Game mới");
		newGameButton.addActionListener(e -> controller.newGame());

		exitButton = new JButton("Thoát");
		exitButton.addActionListener(e -> System.exit(0));

		aiLevelCombo = new JComboBox<>(new String[] { "RandomAI", "MiniMax", "AlphaBeta" });
		aiLevelCombo.addActionListener(e -> {
		    String level = aiLevelCombo.getSelectedItem().toString();
		    switch(level) {
		        case "DemoAI":
		            controller.setAI(new SimpleAI(GamePanel.BLACK));
		            break;
		        case "MiniMax":
		            controller.setAI(new MinimaxAI(GamePanel.BLACK, 3));
		            break;
		        case "AlphaBeta":
		            controller.setAI(new AlphaBetaAI(GamePanel.BLACK, 5));
		            break;
		    }
	
		});

		turnLabel = new JLabel("Lượt: ");
		winnerLabel = new JLabel("");

		buttons.add(newGameButton);
		buttons.add(exitButton);
		buttons.add(aiLevelCombo);
		buttons.add(turnLabel);
		buttons.add(winnerLabel);

		add(buttons, BorderLayout.SOUTH);

		// Timer update
		Timer t = new Timer(50, e -> updateInfo());
		t.start();
	}

	public void addMove(String move) {
		moveLogArea.append(move + "\n");
	}
	// THÔNG BÁO CỦA AI ĐANG SUY NGHĨ
	public void removeLastMove() {
	    String text = moveLogArea.getText();
	    int last = text.lastIndexOf("\n", text.length() - 2);
	    if (last >= 0) {
	        moveLogArea.setText(text.substring(0, last + 1));
	    } else {
	        moveLogArea.setText("");
	    }
	}

	private void updateInfo() {
		if (controller.getWinner() != null) {
			winnerLabel.setText(controller.getWinner() + " THUA!");
			turnLabel.setText("");
		} else {
			turnLabel.setText("LƯƠT CỦA: " + (controller.getCurrentPlayer() == GamePanel.WHITE ? "TRẮNG" : "ĐEN"));
			winnerLabel.setText("");
		}
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}

}
