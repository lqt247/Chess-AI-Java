package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ui.GamePanel;
import utils.ImageLoader;

public class Rook extends Pieces {
	public Rook(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE)

		{
			image = ImageLoader.load("/accet_pieces/w-rook-pieces.png");
		} else {
			image = ImageLoader.load("/accet_pieces/b-rook-pieces.png");
		}
	}
	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    if (!isWithInBoard(targetCol, targetRow)) return false;
	    if (targetCol == col && targetRow == row) return false;

	    if (targetCol == col) { // cùng cột
	        int dir = (targetRow > row) ? 1 : -1;
	        for (int r = row + dir; r != targetRow; r += dir)
	            if (getPiecesAt(col, r) != null) return false;
	        return !isAllyPiece(targetCol, targetRow);
	    } else if (targetRow == row) { // cùng hàng
	        int dir = (targetCol > col) ? 1 : -1;
	        for (int c = col + dir; c != targetCol; c += dir)
	            if (getPiecesAt(c, row) != null) return false;
	        return !isAllyPiece(targetCol, targetRow);
	    }
	    return false;
	}
	
	@Override
	public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
	    int dc = targetCol - col;
	    int dr = targetRow - row;

	    if (dc != 0 && dr != 0) return false; // chỉ đi thẳng
	    if (dc == 0 && dr == 0) return false;

	    int stepC = Integer.compare(dc, 0);
	    int stepR = Integer.compare(dr, 0);

	    int x = col + stepC;
	    int y = row + stepR;

	    while (x != targetCol || y != targetRow) {
	        if (!isEmptySim(board, x, y)) return false;
	        x += (x != targetCol) ? stepC : 0;
	        y += (y != targetRow) ? stepR : 0;
	    }

	    Pieces target = getPiecesAtSim(board, targetCol, targetRow);
	    return target == null || target.color != this.color;
	}







	@Override
	public Pieces copy() {
	    Rook r = new Rook(this.color, this.col, this.row);
	    r.hasMoved = this.hasMoved;
	    r.preCol = this.preCol;
	    r.preRow = this.preRow;
	    return r;
	}




}
