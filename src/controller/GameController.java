package controller;

import ui.GamePanel;
import ui.ControlPanel;
import model.Pieces;
import ai.AI;

import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;


public class GameController {
	private GamePanel gamePanel;
	private ControlPanel controlPanel;
	private int currentPlayer;
	private String winner;
	private MoveLoger moveLogger;
	private boolean lastMovePutKingInCheck = false;
	// LỊCH SỬ - DANH SÁCH
	private ArrayList<String> history = new ArrayList<>();

	private HashMap<String, Integer> positionCount = new HashMap<>();

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

	//  GETTER / SETTER 
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

	// XỬ LÝ NƯỚC ĐI 
	public void onMove(Pieces piece, int oldCol, int oldRow, int newCol, int newRow, Pieces target) {
		if (winner != null)
			return; // game đã kết thúc

		String colorName;
		if (piece.color == GamePanel.WHITE) {
		    colorName = " BẠN ";
		} else {
		    colorName = (ai != null) ? " AI " : "Đen";
		}

		String pieceName = piece.getClass().getSimpleName();

		String move = pieceName + " (" + colorName + "): " + toChessNotation(oldCol, oldRow) + " -> "
				+ toChessNotation(newCol, newRow);
		if (target != null)
			move += " ĂN: " + target.getClass().getSimpleName();

		moveLogger.addMove(move);
		if (controlPanel != null)
			controlPanel.addMove(move);

		// 1. Check / Checkmate
		checkCheckAndCheckmate(piece);
		if (winner != null) return;

		// 2. ĐỔI LƯỢT
		currentPlayer = (piece.color == GamePanel.WHITE)
		        ? GamePanel.BLACK
		        : GamePanel.WHITE;

		// 3. CHECK LẶP VỊ TRÍ (SAU KHI ĐỔI LƯỢT)
		if (detectRepetitionByPosition()) {
		    winner = "Hòa";
		    currentPlayer = -1;
		    if (controlPanel != null)
		        controlPanel.addMove("=== HÒA (Lặp vị trí 3 lần) ===");
		    return;
		}

		// 4. GỌI AI
		callAIIfNeeded();



		//  KIỂM TRA CHECK / CHECKMATE TRƯỚC KHI ĐỔI LƯỢT 
		checkCheckAndCheckmate(piece);

		// Nếu game kết thúc → không đổi lượt, không gọi AI
		if (winner != null)
			return;

		//  ĐỔI LƯỢT 
		currentPlayer = (piece.color == GamePanel.WHITE) ? GamePanel.BLACK : GamePanel.WHITE;

		//  GỌI AI
		callAIIfNeeded();

	}

	//GỌI AI (thread riêng, an toàn)
	private void callAIIfNeeded() {

	    //  ĐIỀU KIỆN CHẶN 
	    if (ai == null) return;
	    if (winner != null) return;
	    if (currentPlayer != GamePanel.BLACK) return; // AI chơi ĐEN
	    if (aiThinking) return;

	    aiThinking = true;

	    // HIỆN "AI ĐANG SUY NGHĨ"
	    if (controlPanel != null) {
	        javax.swing.SwingUtilities.invokeLater(() -> {
	        
	        	
	        	controlPanel.addMove("------------------------------------------------------");
	        	controlPanel.addMove("          🤖 AI ĐANG SUY NGHĨ...");
	        	controlPanel.addMove("------------------------------------------------------");

;
	        });
	    }

	    //  THREAD RIÊNG CHO AI
	    new Thread(() -> {

	        int[] aiMove = null;

	        try {
	            Thread.sleep(300); // delay cho cảm giác AI suy nghĩ

	            if (winner != null) return;

	            // AI TÍNH TOÁN 
	            aiMove = ai.chooseMove(GamePanel.pieces, moveLogger.getMoves());

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        final int[] finalMove = aiMove;

	        // QUAY LẠI EDT
	        javax.swing.SwingUtilities.invokeLater(() -> {
	            try {
	                // 1-XOÁ DÒNG "AI ĐANG SUY NGHĨ..."
	                if (controlPanel != null) {
	                	controlPanel.removeLastMove();
	                	controlPanel.removeLastMove();
	                	controlPanel.removeLastMove();

	                }

	                // 2-CHO AI ĐI (sẽ tự log trong onMove)
	                if (finalMove != null && winner == null && gamePanel != null) {
	                    gamePanel.applyAIMove(finalMove);
	                }

	            } finally {
	                aiThinking = false;
	            }
	        });

	    }).start();
	}


	// KIỂM TRA CHECK / CHECKMATE 
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
					controlPanel.addMove("=== " + winner + " THUA (Chiếu bí) ===");
			} else {
				// Không bị chiếu nhưng không còn nước → Stalemate (hòa)
				winner = "Hòa";
				if (controlPanel != null)
					controlPanel.addMove("=== HÒA ===");
			}
			currentPlayer = -1; // game kết thúc
		}
	}

	//
	private String encodeBoard(ArrayList<Pieces> board, int turnColor) {
	    StringBuilder sb = new StringBuilder();

	    board.stream()
	        .sorted((a, b) -> {
	            int c = a.getClass().getSimpleName()
	                    .compareTo(b.getClass().getSimpleName());
	            if (c != 0) return c;
	            if (a.color != b.color) return a.color - b.color;
	            if (a.col != b.col) return a.col - b.col;
	            return a.row - b.row;
	        })
	        .forEach(p -> {
	            sb.append(p.getClass().getSimpleName())
	              .append(p.color)
	              .append(p.col)
	              .append(p.row)
	              .append(";");
	        });

	    sb.append("T").append(turnColor);
	    return sb.toString();
	}
 String encodeMove(Pieces p, int[] mv) {
	    String colorName = (p.color == 1) ? "Trắng" : "Đen";
	    String pieceName = p.getClass().getSimpleName();
	    return pieceName + " (" + colorName + "): "
	         + toChess(mv[0], mv[1]) + " -> " + toChess(mv[2], mv[3]);
	}

	private String toChess(int c, int r) {
	    return "" + (char)('a' + c) + (8 - r);
	}

	// NEW GAME: GAME MỚI
	public void newGame() {
		if (gamePanel != null)
			gamePanel.resetBoard();
		// XÓA 2LIGHT
		gamePanel.clearLastMoveHighlight();
		// XÓA LOG
		moveLogger.clear();
		
		positionCount.clear();
		// SET NGƯỜI CHƠI NÀ
		currentPlayer = GamePanel.WHITE;
		winner = null;
		aiThinking = false;
		lastMovePutKingInCheck = false;
		if (controlPanel != null)
			controlPanel.addMove("___________GAME MỚI___________");
	}

	// HỖ TRỢ CHUYỂN TOẠ DO CHESS NOTATION
	private String toChessNotation(int col, int row) {
		char file = (char) ('a' + col);
		int rank = 8 - row;
		return "" + file + rank;
	}
	// Hàm này dùng để check 2 nước đi lặp ( dạy cho AI biết nen trách lặp -> không là xử HÒA )
	public boolean isRepeatMove(String move) {
	    List<String> list = moveLogger.getMoves();
	    int n = list.size();
	    if (n < 4) return false;

	    // so với 2 lần trước
	    return move.equals(list.get(n - 2));
	}

	// Hàm này dùng để check nếu quá 3 bước lập thì sẽ trả về HÒA nha
	private boolean detectRepetitionByPosition() {
	    String key = encodeBoard(GamePanel.pieces, currentPlayer);

	    int count = positionCount.getOrDefault(key, 0) + 1;
	    positionCount.put(key, count);

	    return count >= 3;
	}


}
