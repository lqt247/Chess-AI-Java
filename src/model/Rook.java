package model;

import java.awt.image.BufferedImage;

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


}
