package controller;

import ui.GamePanel;
import ui.ControlPanel;
import model.Pieces;
import ai.AI;
import ai.SimpleAI;

public class GameController {
	private GamePanel gamePanel;
	private ControlPanel controlPanel;
	private int currentPlayer;
	private String winner;
	// Move Logger
	private MoveLoger moveLogger;
	// AI player
	private AI ai; 

	public GameController(GamePanel gp, ControlPanel cp, AI ai) {
		this.gamePanel = gp;
		this.controlPanel = cp;
		this.ai = ai;
		this.currentPlayer = GamePanel.WHITE;
		this.winner = null;
		this.moveLogger = new MoveLoger();
	}

	// THIẾT LẬP NGƯỜI CHƠI
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public String getWinner() {
		return winner;
	}

	public AI getAI() {
		return ai;
	}
	// METHOD: Ghi lại nước đi, kiểm tra thắng, đổi lượt
	public void onMove(Pieces piece, int oldCol, int oldRow, int newCol, int newRow, Pieces target) {
		String colorName = (piece.color == GamePanel.WHITE) ? "Trắng" : "Đen";
		String pieceName = piece.getClass().getSimpleName();

		String move = pieceName + " (" + colorName + "): " + toChessNotation(oldCol, oldRow) + " -> "
				+ toChessNotation(newCol, newRow);
		if (target != null)
			move += " ĂN: " + target.getClass().getSimpleName();
		moveLogger.addMove(move);

		if (controlPanel != null)
			controlPanel.addMove(move);

		if (target instanceof model.King) {
			winner = colorName;
		}

		if (winner == null)
			currentPlayer = (currentPlayer == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;
	}
	
	// METHOD: Cái này để xem vị trí - MoveLogger
	// Rõ hơn: Chuyển tọa độ sang ký hiệu cờ vua (ví dụ: 0,0 -> a8)

	private String toChessNotation(int col, int row) {
		char file = (char) ('a' + col);
		int rank = 8 - row;
		return "" + file + rank;
	}
	
	// METHOD: Game mới -> nó sẽ tắt Thread
	public void newGame() {
		gamePanel.resetBoard();
		moveLogger.clear();
		currentPlayer = GamePanel.WHITE;
		winner = null;
		if (controlPanel != null)
			controlPanel.addMove("___________GAME MỚI___________");
	}
	
	// METHOD: Khai báo chọn AI (có thể là SimpleAI, MediumAI, HardAI...)
	public void setAI(SimpleAI simpleAI) {
		this.ai = simpleAI;

	}
}
