package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ui.GamePanel;

public class Pieces {
    public BufferedImage image;
    public int col, row;
    public int preCol, preRow;
    public int color;
    public int x, y;
    public boolean hasMoved = false;


    public Pieces(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
    }

    // GAME THẬT 
    protected Pieces getPiecesAt(int col, int row) {
        for (Pieces p : GamePanel.pieces) {
            if (p.col == col && p.row == row)
                return p;
        }
        return null;
    }

    //  AI 
    protected Pieces getPiecesAt(ArrayList<Pieces> list, int col, int row) {
        for (Pieces p : list) {
            if (p.col == col && p.row == row)
                return p;
        }
        return null;
    }

    //  COPY ĐÚNG LOẠI QUÂN
    public Pieces copy() {
        if (this instanceof Pawn)   return new Pawn(color, col, row);
        if (this instanceof Rook)   return new Rook(color, col, row);
        if (this instanceof Knight) return new Knight(color, col, row);
        if (this instanceof Bishop) return new Bishop(color, col, row);
        if (this instanceof Queen)  return new Queen(color, col, row);
        if (this instanceof King)   return new King(color, col, row);
        return new Pieces(color, col, row);
    }
    public Pieces copyForAI() {
        Pieces p;
        if (this instanceof Pawn)      p = new Pawn(color, col, row, false);
        else if (this instanceof Rook) p = new Rook(color, col, row, false);
        else if (this instanceof Knight) p = new Knight(color, col, row, false);
        else if (this instanceof Bishop) p = new Bishop(color, col, row, false);
        else if (this instanceof Queen) p = new Queen(color, col, row, false);
        else if (this instanceof King) p = new King(color, col, row, false);
        else p = new Pieces(color, col, row);

        p.hasMoved = this.hasMoved;
        return p;
    }


    public int getX(int col) {
        return Board.offsetX + col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return Board.offsetY + row * Board.SQUARE_SIZE;
    }

    public boolean isWithInBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    // BASE METHOD 
    public boolean canMove(int targetCol, int targetRow) {
        if (!isWithInBoard(targetCol, targetRow)) return false;
        if (targetCol == col && targetRow == row) return false;
        return false;
    }

    // ALLY CHECK 
    protected boolean isAllyPiece(int targetCol, int targetRow) {
        Pieces target = getPiecesAt(targetCol, targetRow);
        return target != null && target.color == this.color;
    }

    protected boolean isAllyPiece(ArrayList<Pieces> list, int targetCol, int targetRow) {
        Pieces target = getPiecesAt(list, targetCol, targetRow);
        return target != null && target.color == this.color;
    }

    public void draw(Graphics2D g2) {
        if (image != null)
            g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    // ===== AI SUPPORT =====
    public ArrayList<int[]> getValidMoves(ArrayList<Pieces> board) {
        return new ArrayList<>();
    }

    public int getValue() {
        return 0;
    }

    // ===== SUPPORT SIMULATION =====
    protected Pieces getPiecesAtSim(ArrayList<Pieces> board, int col, int row) {
        for (Pieces p : board) {
            if (p.col == col && p.row == row)
                return p;
        }
        return null;
    }

    protected boolean isEnemySim(ArrayList<Pieces> board, int col, int row) {
        Pieces p = getPiecesAtSim(board, col, row);
        return p != null && p.color != this.color;
    }

    protected boolean isEmptySim(ArrayList<Pieces> board, int col, int row) {
        return getPiecesAtSim(board, col, row) == null;
    }

    //MẶC ĐỊNH CHO SIMULATION / AI 
    // Rất an toàn, không bao giờ đứng game
    public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
        // Mặc định: chỉ kiểm tra ô trống / đồng minh, không logic quân
        if (!isWithInBoard(targetCol, targetRow)) return false;
        if (targetCol == col && targetRow == row) return false;

        Pieces target = getPiecesAtSim(board, targetCol, targetRow);
        return target == null || target.color != this.color;
    }
}
