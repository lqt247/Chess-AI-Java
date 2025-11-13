package model;

import java.awt.image.BufferedImage;

import ui.GamePanel;
import utils.ImageLoader;

public class King extends Pieces {
	public King(int color, int col, int row) {
		super(color, col, row);
		if (color == GamePanel.WHITE) {
			image = ImageLoader.load("/accet_pieces/w-king-pieces.png");
		} else {
			image = ImageLoader.load("/accet_pieces/b-king-pieces.png");

		}

	}
	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    if (!isWithInBoard(targetCol, targetRow)) return false;

	    int dCol = Math.abs(targetCol - col);
	    int dRow = Math.abs(targetRow - row);

	    if (dCol <= 1 && dRow <= 1)
	        return !isAllyPiece(targetCol, targetRow);

	    return false;
	}

}
