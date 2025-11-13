package model;

import java.awt.image.BufferedImage;

import ui.GamePanel;
import utils.ImageLoader;

public class Knight extends Pieces {
	public Knight(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = ImageLoader.load("/accet_pieces/w-knight-pieces.png");
		} else {
			image = ImageLoader.load("/accet_pieces/b-knight-pieces.png");
		}
	}
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    if (!isWithInBoard(targetCol, targetRow)) return false;

	    int dCol = Math.abs(targetCol - col);
	    int dRow = Math.abs(targetRow - row);

	    if ((dCol == 2 && dRow == 1) || (dCol == 1 && dRow == 2))
	        return !isAllyPiece(targetCol, targetRow);

	    return false;
	}

}
