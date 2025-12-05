package controller;

import ui.GamePanel;
import ui.ControlPanel;
import model.Pieces;
import ai.AI;

import java.util.ArrayList;

/**
 * GameController (full, đã sửa) - Chịu trách nhiệm ghi log, đổi lượt, gọi AI
 * (trên thread riêng) - setAI(AI) để ControlPanel có thể gán SimpleAI /
 * MinimaxAI / AlphaBeta
 */
public class GameController {
	private GamePanel gamePanel;
	private ControlPanel controlPanel;
	private int currentPlayer;
	private String winner;
	private MoveLoger moveLogger;
	private boolean lastMovePutKingInCheck = false;

	private AI ai; // AI tổng quát (SimpleAI / MinimaxAI)
	private volatile boolean aiThinking = false; // tránh gọi AI liên tục

	public GameController(GamePanel gp, ControlPanel cp, AI ai) {
		this.gamePanel = gp;
		this.controlPanel = cp;
		this.ai = ai;
		this.currentPlayer = GamePanel.WHITE;
		this.winner = null;
		this.moveLogger = new MoveLoger();
	}

	// ===== GETTER / SETTER =====
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public String getWinner() {
		return winner;
	}

	public AI getAI() {
		return ai;
	}

	public void setAI(AI ai) {
		this.ai = ai;
		this.aiThinking = false;
	}

	public void setControlPanel(ControlPanel cp) {
		this.controlPanel = cp;
	}

	public void setWinner(String w) {
		this.winner = w;
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}

	// ===== XỬ LÝ NƯỚC ĐI =====
	public void onMove(Pieces piece, int oldCol, int oldRow, int newCol, int newRow, Pieces target) {
		if (winner != null)
			return; // game đã kết thúc

		String colorName = (piece.color == GamePanel.WHITE) ? "Trắng" : "Đen";
		String pieceName = piece.getClass().getSimpleName();

		String move = pieceName + " (" + colorName + "): " + toChessNotation(oldCol, oldRow) + " -> "
				+ toChessNotation(newCol, newRow);
		if (target != null)
			move += " ĂN: " + target.getClass().getSimpleName();

		moveLogger.addMove(move);
		if (controlPanel != null)
			controlPanel.addMove(move);

		// ===== KIỂM TRA CHECK / CHECKMATE TRƯỚC KHI ĐỔI LƯỢT =====
		checkCheckAndCheckmate(piece);

		// Nếu game kết thúc → không đổi lượt, không gọi AI
		if (winner != null)
			return;
		

		// ===== ĐỔI LƯỢT =====
		currentPlayer = (piece.color == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;

		// ===== GỌI AI =====
		callAIIfNeeded();

	}

	// ===== GỌI AI (thread riêng, an toàn) =====
	private void callAIIfNeeded() {
		if (ai == null || winner != null)
			return;
		if (currentPlayer != GamePanel.BLACK)
			return; // giả sử AI luôn chơi ĐEN
		if (aiThinking)
			return;

		aiThinking = true;

		new Thread(() -> {
			try {
				Thread.sleep(300); // delay nhỏ cho cảm giác AI suy nghĩ

				if (winner != null)
					return; // game kết thúc → AI không đi

				int[] move = ai.chooseMove(GamePanel.pieces);

				if (move != null && winner == null) {
					javax.swing.SwingUtilities.invokeLater(() -> {
						if (gamePanel != null) {
							gamePanel.applyAIMove(move);
						}
					});
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				aiThinking = false;
			}
		}).start();
	}

	// ===== KIỂM TRA CHECK / CHECKMATE =====
	private void checkCheckAndCheckmate(Pieces movedPiece) {
		if (winner != null)
			return;

		int enemyColor = (movedPiece.color == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;
		String enemyName = (enemyColor == GamePanel.WHITE) ? "Trắng" : "Đen";
		boolean inCheck = Rules.isKingInCheck(GamePanel.pieces, enemyColor);

		// Thông báo CHIẾU (chỉ một lần)
		if (inCheck && !lastMovePutKingInCheck) {
			if (controlPanel != null)
				controlPanel.addMove("⚠️ " + enemyName + " bị CHIẾU!");
			lastMovePutKingInCheck = true;
		} else if (!inCheck) {
			lastMovePutKingInCheck = false;
		}

		// Kiểm tra tất cả nước đi hợp lệ của đối phương
		ArrayList<int[]> legal = Rules.getLegalMoves(GamePanel.pieces, enemyColor);


		if (legal.isEmpty()) {
			if (inCheck) {
				// Vua đang bị chiếu + không còn nước → Checkmate
				winner = (enemyColor == GamePanel.WHITE) ? "Trắng" : "Đen";
				if (controlPanel != null)
					controlPanel.addMove("=== " + winner + " CHIẾN THẮNG (Checkmate) ===");
			} else {
				// Không bị chiếu nhưng không còn nước → Stalemate (hòa)
				winner = "Hòa";
				if (controlPanel != null)
					controlPanel.addMove("=== HÒA (Stalemate) ===");
			}
			currentPlayer = -1; // game kết thúc
		}
	}

	// ===== NEW GAME =====
	public void newGame() {
		if (gamePanel != null)
			gamePanel.resetBoard();
		moveLogger.clear();
		currentPlayer = GamePanel.WHITE;
		winner = null;
		aiThinking = false;
		lastMovePutKingInCheck = false;
		if (controlPanel != null)
			controlPanel.addMove("___________GAME MỚI___________");
	}

	// ===== HỖ TRỢ CHUYỂN TOẠ DO CHESS NOTATION =====
	private String toChessNotation(int col, int row) {
		char file = (char) ('a' + col);
		int rank = 8 - row;
		return "" + file + rank;
	}
}
