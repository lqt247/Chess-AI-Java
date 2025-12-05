package ui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;

import controller.GameController;
import model.*;
import controller.Rules;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public GameController controller;

	final int MAX_WIDTH = 1000;
	final int MAX_HEIGHT = 1000;

	Board board = new Board();
	MouseHandler mouse = new MouseHandler();
	public static ArrayList<Pieces> pieces = new ArrayList<>();
	public static Pieces[][] boardMap = new Pieces[8][8];

	Pieces activePiece;
	ArrayList<int[]> validSquares = new ArrayList<>();

	public static final int WHITE = 1;
	public static final int BLACK = 0;

	public GamePanel() {
		setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		setBackground(new Color(0x2E, 0x66, 0x33));
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		setPieces();
	    new javax.swing.Timer(16, e -> update()).start();

	}



	public void setPieces() {
		pieces.clear();
		boardMap = new Pieces[8][8];

		for (int i = 0; i < 8; i++)
			addPiece(new Pawn(WHITE, i, 6));
		addPiece(new King(WHITE, 4, 7));
		addPiece(new Queen(WHITE, 3, 7));
		addPiece(new Bishop(WHITE, 2, 7));
		addPiece(new Bishop(WHITE, 5, 7));
		addPiece(new Rook(WHITE, 0, 7));
		addPiece(new Rook(WHITE, 7, 7));
		addPiece(new Knight(WHITE, 1, 7));
		addPiece(new Knight(WHITE, 6, 7));

		for (int i = 0; i < 8; i++)
			addPiece(new Pawn(BLACK, i, 1));
		addPiece(new King(BLACK, 4, 0));
		addPiece(new Queen(BLACK, 3, 0));
		addPiece(new Bishop(BLACK, 2, 0));
		addPiece(new Bishop(BLACK, 5, 0));
		addPiece(new Rook(BLACK, 0, 0));
		addPiece(new Rook(BLACK, 7, 0));
		addPiece(new Knight(BLACK, 1, 0));
		addPiece(new Knight(BLACK, 6, 0));

		syncBoardMap();
	

	}

	

	public void update() {
		// nếu game đã kết thúc thì không cho xử lý click
		if (controller != null && controller.getWinner() != null)
			return;

		if (mouse.clicked) {
			int clickedCol = (mouse.mouseX - Board.offsetX) / Board.SQUARE_SIZE;
			int clickedRow = (mouse.mouseY - Board.offsetY) / Board.SQUARE_SIZE;
			mouse.clicked = false;
			repaint();


			if (clickedCol < 0 || clickedCol > 7 || clickedRow < 0 || clickedRow > 7)
				return;

			int turn = controller != null ? controller.getCurrentPlayer() : WHITE;
			boolean gameOver = controller != null && controller.getWinner() != null;
			boolean notYourTurn = controller != null && turn != WHITE; // giả sử người chơi luôn WHITE

			Pieces clickedPiece = getPieceAt(clickedCol, clickedRow);

			if (activePiece == null) {
				if (clickedPiece != null && clickedPiece.color == turn && !notYourTurn) {
					activePiece = clickedPiece;
					simulateValidMoves(activePiece); // now uses Rules.getLegalMoves
				}
			} else if (activePiece != null && !gameOver && !notYourTurn && !validSquares.isEmpty()) {

				// Trước khi gọi move, kiểm tra nước này có trong danh sách legal (được simulateValidMoves chuẩn hoá)
				boolean found = false;
				for (int[] sq : validSquares) {
					if (sq[0] == clickedCol && sq[1] == clickedRow) {
						found = true;
						break;
					}
				}
				if (found) {
					movePiece(activePiece, clickedCol, clickedRow);
				} else {
					// không phải nước hợp lệ — bỏ chọn (nếu muốn có thông báo)
					if (controller != null && controller.getControlPanel() != null) {
						controller.getControlPanel().addMove("⚠️ Bạn cần chọn và di chuyển quân cờ!");
					}
				}
				activePiece = null;
				validSquares.clear();
				
			}
		}

	}

	private void addPiece(Pieces p) {
		pieces.add(p);
		if (p.col >= 0 && p.col < 8 && p.row >= 0 && p.row < 8)
			boardMap[p.col][p.row] = p;
	}

	private void syncBoardMap() {
		for (int c = 0; c < 8; c++)
			for (int r = 0; r < 8; r++)
				boardMap[c][r] = null;

		for (Pieces p : pieces) {
			if (p != null && p.col >= 0 && p.col < 8 && p.row >= 0 && p.row < 8) {
				boardMap[p.col][p.row] = p;
			}
		}
	}

	// ==== SỬ DỤNG Rules.getLegalMoves ĐỂ LẤY CÁC NƯỚC THỰC SỰ HỢP LỆ ====
	// in GamePanel.java
	private void simulateValidMoves(Pieces p) {
	    validSquares.clear();
	    if (p == null || controller == null) return;

	    boolean hasLegalMove = false; // để biết quân này có ít nhất 1 nước đi hợp lệ

	    for (int c = 0; c < 8; c++) {
	        for (int r = 0; r < 8; r++) {

	            if (c == p.col && r == p.row) continue;

	            // Bỏ qua ô mà canMoveSim chắc chắn false
	            if (!p.canMoveSim(pieces, c, r)) continue;

	            int[] move = {p.col, p.row, c, r};

	            boolean legal = false;
	            try {
	                legal = Rules.isLegalAfterMove(pieces, move, p.color);
	            } catch (Exception ex) {
	                System.err.println("Error simulating move from (" + p.col + "," + p.row + ") to (" + c + "," + r + ")");
	                ex.printStackTrace();
	                continue; // skip ô gây lỗi
	            }

	            if (legal) {
	                validSquares.add(new int[]{c, r});
	                hasLegalMove = true;
	            }
	        }
	    }

	    // Nếu quân này hoàn toàn bị chặn (không có nước hợp lệ)
	    if (!hasLegalMove) {
	        activePiece = null;
	        if (controller != null && controller.getControlPanel() != null) {
	            String pieceName = p.getClass().getSimpleName();
	            controller.getControlPanel().addMove("⚠️ " + pieceName + " đang bị chặn, không có nước đi hợp lệ!");
	        }
	    }
	}





	// Hàm di chuyển trung tâm (người và AI đều dùng)
	private void movePiece(Pieces piece, int toCol, int toRow) {
	    if (piece == null) return;

	    // ✅ 1. KIỂM TRA TỰ CHIẾU TRƯỚC KHI ĐI (CỰC KỲ QUAN TRỌNG)
	    int[] moveCheck = new int[]{ piece.col, piece.row, toCol, toRow };
	    if (!Rules.isLegalAfterMove(pieces, moveCheck, piece.color)) {
	        // ❌ nước đi làm vua tự chiếu -> huỷ
	        return;
	    }

	    Pieces target = getPieceAt(toCol, toRow);
	    if (target instanceof King) return;

	    int oldCol = piece.col, oldRow = piece.row;

	    boardMap[oldCol][oldRow] = null;

	    piece.col = toCol;
	    piece.row = toRow;
	    piece.x = piece.getX(toCol);
	    piece.y = piece.getY(toRow);
	    piece.hasMoved = true;

	    boardMap[toCol][toRow] = piece;

	    if (target != null)
	        pieces.remove(target);

	    // ✅ Phong hậu
	    if (piece instanceof Pawn) {
	        if ((piece.color == WHITE && toRow == 0) || (piece.color == BLACK && toRow == 7)) {
	            Pieces newQueen = new Queen(piece.color, toCol, toRow);
	            int idx = pieces.indexOf(piece);
	            if (idx != -1)
	                pieces.set(idx, newQueen);
	            boardMap[toCol][toRow] = newQueen;
	            piece = newQueen;
	        }
	    }

	    // ✅ Nhập thành
	    if (piece instanceof King && Math.abs(oldCol - toCol) == 2) {
	        castleRook(piece, oldCol, toRow);
	    }

	    // ✅ Báo cho controller
	    if (controller != null) {
	        controller.onMove(piece, oldCol, oldRow, toCol, toRow, target);
	    }

	    repaint();
	}


	private void castleRook(Pieces king, int oldCol, int row) {
		if (king.col == 2) {
			Pieces rook = getPieceAt(0, row);
			if (rook instanceof Rook) {
				boardMap[0][row] = null;
				rook.col = 3;
				rook.row = row;
				rook.x = rook.getX(3);
				rook.hasMoved = true;
				boardMap[3][row] = rook;
			}
		} else if (king.col == 6) {
			Pieces rook = getPieceAt(7, row);
			if (rook instanceof Rook) {
				boardMap[7][row] = null;
				rook.col = 5;
				rook.row = row;
				rook.x = rook.getX(5);
				rook.hasMoved = true;
				boardMap[5][row] = rook;
			}
		}
	}

	public Pieces getPieceAt(int col, int row) {
		if (col < 0 || col > 7 || row < 0 || row > 7)
			return null;
		return boardMap[col][row];
	}

	public void resetBoard() {
		pieces.clear();
		setPieces();
		activePiece = null;
		validSquares.clear();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		board.draw(g2);

		g2.setColor(new Color(50, 205, 50, 180));
		g2.setStroke(new BasicStroke(8));
		for (int[] sq : validSquares) {
			int x = Board.offsetX + sq[0] * Board.SQUARE_SIZE;
			int y = Board.offsetY + sq[1] * Board.SQUARE_SIZE;
			g2.drawRect(x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
		}

		if (activePiece != null) {
			g2.setColor(new Color(200, 0, 0, 120));
			g2.fillRect(Board.offsetX + activePiece.col * Board.SQUARE_SIZE,
					Board.offsetY + activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
		}

		for (Pieces p : pieces)
			p.draw(g2);
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}

	public void applyAIMove(int[] move) {
		if (move == null || move.length < 4)
			return;

		int fromCol = move[0], fromRow = move[1], toCol = move[2], toRow = move[3];
		Pieces moving = getPieceAt(fromCol, fromRow);

		if (moving != null) {
			// apply through movePiece which will validate and call controller.onMove
			movePiece(moving, toCol, toRow);
		}
	}
}
