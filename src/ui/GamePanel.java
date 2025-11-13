package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

import ai.SimpleAI;
import controller.GameController;
import model.*;

public class GamePanel extends JPanel implements Runnable {
	public GameController controller;
	// SET:
	final int MAX_WIDTH = 1000;
	final int MAX_HEIGHT = 1000;
	final int FPS = 60;
	// THREAD: cho nó 1 luồng riêng
	Thread gameThread;
	// Bàn cờ
	Board board = new Board();
	// Xử lý sự kiện chuột
	MouseHandler mouse = new MouseHandler();
	// Tạo quân cờ
	public static ArrayList<Pieces> pieces = new ArrayList<>();
	Pieces activePiece;
	// Tạo màu
	public static final int WHITE = 1;
	public static final int BLACK = 0;
	// Set Default
	int currentColor = WHITE;
       //Tạo một danh sách (ArrayList) chứa các mảng số nguyên (int[])
       //— mỗi mảng int[] biểu diễn tọa độ ô hợp lệ trên bàn cờ.
	ArrayList<int[]> validSquares = new ArrayList<>();

	// TEST AI
	private SimpleAI ai;
	private boolean vsAI = true; // bật/tắt AI

	
	
	public GamePanel() {
		setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		setBackground(new Color(0x2E, 0x66, 0x33));

		addMouseListener(mouse);
		addMouseMotionListener(mouse);

		setPieces();

		// Khơi tạo AI TEST
		ai = new SimpleAI(GamePanel.BLACK);

	}

	
	
	// CHẠY GAME
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	// ĐẶT QUÂN CỜ
	public void setPieces() {
		// WHITE_TEAM
		for (int i = 0; i < 8; i++)
			pieces.add(new Pawn(WHITE, i, 6));
		pieces.add(new King(WHITE, 4, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));

		// BLACK_TEAM
		for (int i = 0; i < 8; i++)
			pieces.add(new Pawn(BLACK, i, 1));
		pieces.add(new King(BLACK, 4, 0));
		pieces.add(new Queen(BLACK, 3, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
	}
	// CHẠY - FPS
	@Override
	public void run() {
		double drawInterval = 1000000000.0 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;

			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	// VẼ - UPDATE: các thay đổi
	public void update() {
		// Nếu đã có người thắng, không update nữa
		if (controller != null && controller.getWinner() != null) {
			return;
		}

		if (mouse.clicked) {
			int clickedCol = (mouse.mouseX - Board.offsetX) / Board.SQUARE_SIZE;
			int clickedRow = (mouse.mouseY - Board.offsetY) / Board.SQUARE_SIZE;

			// Click ngoài board
			if (clickedCol < 0 || clickedCol > 7 || clickedRow < 0 || clickedRow > 7) {
				mouse.clicked = false;
				return;
			}

			Pieces clickedPiece = getPieceAt(clickedCol, clickedRow);

			// Chọn quân
			if (activePiece == null) {
				if (clickedPiece != null && clickedPiece.color == currentColor) {
					activePiece = clickedPiece;
					simulateValidMoves(activePiece);
				}
			}
			// Di chuyển quân
			else {
				boolean moved = false;
				for (int[] sq : validSquares) {
					if (sq[0] == clickedCol && sq[1] == clickedRow) {
						movePiece(activePiece, clickedCol, clickedRow);
						moved = true;
						break;
					}
				}

				activePiece = null;
				validSquares.clear();

				if (moved) {
					currentColor = (currentColor == WHITE) ? BLACK : WHITE;

					// Nếu đã thắng, không cho AI đi
					if (vsAI && currentColor == BLACK && controller.getWinner() == null && controller.getAI() != null) {
						int[] move = controller.getAI().chooseMove(pieces);
						if (move != null) {
							Pieces p = pieces.get(move[4]);
							movePiece(p, move[2], move[3]);
							currentColor = WHITE;
						}
					}
				}

			}
		}

		mouse.clicked = false;
	}
	// tìm các ô hợp lệ mà quân p có thể di chuyển đến,
	// và lưu lại trong danh sách validSquares.
	private void simulateValidMoves(Pieces p) {
		validSquares.clear();
		for (int c = 0; c < 8; c++) {
			for (int r = 0; r < 8; r++) {
				if (p.canMove(c, r)) {
					validSquares.add(new int[] { c, r });
				}
			}
		}
	}
	// DINH CHUYỂN
	private void movePiece(Pieces piece, int col, int row) {
		Pieces target = getPieceAt(col, row);

		int oldCol = piece.col; // tọa độ trước khi di chuyển
		int oldRow = piece.row;

		// Cập nhật vị trí quân
		piece.col = col;
		piece.row = row;
		piece.x = piece.getX(col);
		piece.y = piece.getY(row);

		// Thông báo cho controller để log
		controller.onMove(piece, oldCol, oldRow, col, row, target);

		// Xử lý phong hậu cho Pawn
		if (piece instanceof Pawn) {
			if ((piece.color == WHITE && row == 0) || (piece.color == BLACK && row == 7)) {
				Pieces newQueen = new Queen(piece.color, col, row);
				pieces.remove(piece);
				pieces.add(newQueen);
			}
		}

		// Xóa quân bị ăn
		if (target != null)
			pieces.remove(target);
		// NGĂT GAME NGAY KHI CÓ NGƯỜI THẮNG
		if (controller.getWinner() != null) {
			System.out.println(controller.getWinner() + " thắng!");
			return; // không cho AI đi nữa
		}
	}
	// Trả về quân cờ tại vị trí (col, row), nếu ô trống thì trả về null
	public Pieces getPieceAt(int col, int row) {
		for (Pieces p : pieces) {
			if (p.col == col && p.row == row)
				return p;
		}
		return null;
	}
	// Khởi tạo lại bàn cờ cho ván mới - NÚT: GAME MỚI
	public void resetBoard() {
		pieces.clear();
		setPieces();
		activePiece = null;
		validSquares.clear();
		currentColor = WHITE;
		gameThread = new Thread(this);
		gameThread.start();
	}
	// VẼ 
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		board.draw(g2);

		// Highlight ô hợp lệ
		if (!validSquares.isEmpty()) {
			g2.setColor(new Color(50, 205, 50, 180));
			g2.setStroke(new BasicStroke(10));
			for (int[] sq : validSquares) {
				int x = Board.offsetX + sq[0] * Board.SQUARE_SIZE;
				int y = Board.offsetY + sq[1] * Board.SQUARE_SIZE;
				g2.drawRect(x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
			}

		}
		if (activePiece != null) {
			g2.setColor(new Color(200, 0, 0, 180));
			g2.fillRect(Board.offsetX + activePiece.col * Board.SQUARE_SIZE,
					Board.offsetY + activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
		}

		// Vẽ quân
		for (Pieces p : pieces) {
			p.draw(g2);
		}

		g2.dispose();
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}

}
