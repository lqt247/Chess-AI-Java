package ui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;

import ai.SimpleAI;
import controller.GameController;
import model.*;

public class GamePanel extends JPanel implements Runnable {
    public GameController controller;

    final int MAX_WIDTH = 1000;
    final int MAX_HEIGHT = 1000;
    final int FPS = 60;
    Thread gameThread;

    Board board = new Board();
    MouseHandler mouse = new MouseHandler();
    public static ArrayList<Pieces> pieces = new ArrayList<>();
    Pieces activePiece;

    public static final int WHITE = 1;
    public static final int BLACK = 0;
    int currentColor = WHITE;
    ArrayList<int[]> validSquares = new ArrayList<>();

    private SimpleAI ai;
    private boolean vsAI = true;

    public GamePanel() {
        setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        setBackground(new Color(0x2E, 0x66, 0x33));

        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        setPieces();
        ai = new SimpleAI(BLACK);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        pieces.clear();
        // WHITE
        for (int i = 0; i < 8; i++) pieces.add(new Pawn(WHITE, i, 6));
        pieces.add(new King(WHITE, 4, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));

        // BLACK
        for (int i = 0; i < 8; i++) pieces.add(new Pawn(BLACK, i, 1));
        pieces.add(new King(BLACK, 4, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try { Thread.sleep(2); } catch (InterruptedException e) {}
        }
    }

    public void update() {
        if (controller != null && controller.getWinner() != null) return;

        if (mouse.clicked) {
            int clickedCol = (mouse.mouseX - Board.offsetX) / Board.SQUARE_SIZE;
            int clickedRow = (mouse.mouseY - Board.offsetY) / Board.SQUARE_SIZE;

            if (clickedCol < 0 || clickedCol > 7 || clickedRow < 0 || clickedRow > 7) {
                mouse.clicked = false;
                return;
            }

            Pieces clickedPiece = getPieceAt(clickedCol, clickedRow);

            if (activePiece == null) {
                if (clickedPiece != null && clickedPiece.color == currentColor) {
                    activePiece = clickedPiece;
                    simulateValidMoves(activePiece);
                }
            } else {
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

                    if (vsAI && currentColor == BLACK && controller.getWinner() == null && controller.getAI() != null) {
                        int[] aiMove = controller.getAI().chooseMove(pieces);
                        if (aiMove != null) {
                            Pieces aiPiece = pieces.get(aiMove[4]);
                            movePiece(aiPiece, aiMove[2], aiMove[3]);
                            currentColor = WHITE;
                        }
                    }
                }
            }

            mouse.clicked = false;
        }
    }

    private void simulateValidMoves(Pieces p) {
        validSquares.clear();
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                if (p.canMove(c, r)) validSquares.add(new int[]{c, r});
            }
        }
    }

    private void movePiece(Pieces piece, int col, int row) {
        Pieces target = getPieceAt(col, row);
        int oldCol = piece.col, oldRow = piece.row;

        // Kiểm tra nhập thành
        if (piece instanceof King && Math.abs(oldCol - col) == 2) castleRook(piece, oldCol, row);

        piece.col = col;
        piece.row = row;
        piece.x = piece.getX(col);
        piece.y = piece.getY(row);

        controller.onMove(piece, oldCol, oldRow, col, row, target);

        // Phong hậu
        if (piece instanceof Pawn) {
            if ((piece.color == WHITE && row == 0) || (piece.color == BLACK && row == 7)) {
                Pieces newQueen = new Queen(piece.color, col, row);
                pieces.remove(piece);
                pieces.add(newQueen);
            }
        }
     // Kiểm tra nhập thành
        if (piece instanceof King && Math.abs(oldCol - col) == 2) {
            castleRook(piece, oldCol, row);
        }
        piece.hasMoved = true;

        
        if (target != null) pieces.remove(target);

        if (controller.getWinner() != null) return;
    }
    private void castleRook(Pieces king, int oldCol, int row) {
        if (king.col == 2) { // long castle
            Rook rook = (Rook) getPieceAt(0, row);
            if (rook != null) {
                rook.col = 3;
                rook.x = rook.getX(3);
                rook.hasMoved = true;
            }
        } else if (king.col == 6) { // short castle
            Rook rook = (Rook) getPieceAt(7, row);
            if (rook != null) {
                rook.col = 5;
                rook.x = rook.getX(5);
                rook.hasMoved = true;
            }
        }
    }

    public Pieces getPieceAt(int col, int row) {
        for (Pieces p : pieces) if (p.col == col && p.row == row) return p;
        return null;
    }

    public void resetBoard() {
        pieces.clear();
        setPieces();
        activePiece = null;
        validSquares.clear();
        currentColor = WHITE;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        board.draw(g2);

        // highlight
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
                    Board.offsetY + activePiece.row * Board.SQUARE_SIZE,
                    Board.SQUARE_SIZE, Board.SQUARE_SIZE);
        }

        for (Pieces p : pieces) p.draw(g2);

        g2.dispose();
    }

    public void setController(GameController controller) { this.controller = controller; }
}
