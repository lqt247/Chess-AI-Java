package model;

import java.awt.image.BufferedImage;

import ui.GamePanel;
import utils.ImageLoader;

public class Queen extends Pieces {
	public Queen(int color, int col, int row) {
		super(color, col, row);
		if (color == GamePanel.WHITE)

		{
			image = ImageLoader.load("/accet_pieces/w-queen-pieces.png");
		} else {
			image = ImageLoader.load("/accet_pieces/b-queen-pieces.png");
		}
	}
	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		if (!isWithInBoard(targetCol, targetRow))
			return false;
		if (targetCol == col && targetRow == row)
			return false;

		Rook r = new Rook(color, col, row);
		Bishop b = new Bishop(color, col, row);
		return r.canMove(targetCol, targetRow) || b.canMove(targetCol, targetRow);
	}

}
