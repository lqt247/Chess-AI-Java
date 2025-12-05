package model;

import java.util.ArrayList;

import ui.GamePanel;
import utils.ImageLoader;

public class Bishop extends Pieces {

    public Bishop(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            image = ImageLoader.load("/accet_pieces/w-bishop-pieces.png");
        } else {
            image = ImageLoader.load("/accet_pieces/b-bishop-pieces.png");
        }
    }

    public Bishop(int color, int col, int row, boolean loadImage) {
        super(color, col, row);
        if (!loadImage) return;
        if (color == GamePanel.WHITE)
            image = ImageLoader.load("/accet_pieces/w-bishop-pieces.png");
        else
            image = ImageLoader.load("/accet_pieces/b-bishop-pieces.png");
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (!isWithInBoard(targetCol, targetRow)) return false;
        if (targetCol == col && targetRow == row) return false;

        int dCol = targetCol - col;
        int dRow = targetRow - row;

        // ✅ Phải đi chéo
        if (Math.abs(dCol) != Math.abs(dRow)) return false;

        int stepCol = (dCol > 0) ? 1 : -1;
        int stepRow = (dRow > 0) ? 1 : -1;

        int c = col + stepCol;
        int r = row + stepRow;

        // ✅ Kiểm tra vật cản trên đường
        while (c != targetCol && r != targetRow) {
            if (getPiecesAt(c, r) != null) return false; // bị chặn
            c += stepCol;
            r += stepRow;
        }

        // ✅ Ô đến không được là quân đồng minh
        return !isAllyPiece(targetCol, targetRow);
    }
    @Override
    public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
        int dc = targetCol - col;
        int dr = targetRow - row;

        if (Math.abs(dc) != Math.abs(dr) || dc == 0) return false;

        int stepC = Integer.compare(dc, 0);
        int stepR = Integer.compare(dr, 0);

        int x = col + stepC;
        int y = row + stepR;

        while (x != targetCol || y != targetRow) {
            if (!isEmptySim(board, x, y)) return false;
            x += stepC;
            y += stepR;
        }

        Pieces target = getPiecesAtSim(board, targetCol, targetRow);
        return target == null || target.color != this.color;
    }







    // ✅ COPY CHUẨN CHO AI / MINIMAX
  
    @Override
    public Pieces copy() {
        Bishop b = new Bishop(color, col, row, false);
        b.hasMoved = this.hasMoved;
        return b;
    }

}
