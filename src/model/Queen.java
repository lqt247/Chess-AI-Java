package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
	public Queen(int color, int col, int row, boolean loadImage) {
	    super(color, col, row);
	    if (!loadImage) return; // AI clone → không load ảnh
	    if (color == GamePanel.WHITE)
	        image = ImageLoader.load("/accet_pieces/w-queen-pieces.png");
	    else
	        image = ImageLoader.load("/accet_pieces/b-queen-pieces.png");
	}

	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		if (!isWithInBoard(targetCol, targetRow))
			return false;
		if (targetCol == col && targetRow == row)
			return false;

		int dCol = targetCol - col;
		int dRow = targetRow - row;

		// ===== ĐI THẲNG =====
		if (dCol == 0 || dRow == 0) {
			int stepCol = Integer.compare(dCol, 0);
			int stepRow = Integer.compare(dRow, 0);

			int c = col + stepCol;
			int r = row + stepRow;

			while (c != targetCol || r != targetRow) {
				if (getPiecesAt(c, r) != null)
					return false;
				c += stepCol;
				r += stepRow;
			}
			return !isAllyPiece(targetCol, targetRow);
		}

		// ===== ĐI CHÉO =====
		if (Math.abs(dCol) == Math.abs(dRow)) {
			int stepCol = Integer.compare(dCol, 0);
			int stepRow = Integer.compare(dRow, 0);

			int c = col + stepCol;
			int r = row + stepRow;

			while (c != targetCol || r != targetRow) {
				if (getPiecesAt(c, r) != null)
					return false;
				c += stepCol;
				r += stepRow;
			}

			return !isAllyPiece(targetCol, targetRow);
		}

		return false;
	}

	
	@Override
	public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
	    int dc = targetCol - col;
	    int dr = targetRow - row;

	    if (dc == 0 && dr == 0) return false;

	    int stepC = Integer.compare(dc, 0);
	    int stepR = Integer.compare(dr, 0);

	    if (stepC != 0 && stepR != 0 && Math.abs(dc) != Math.abs(dr)) return false;

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
	public Pieces copyForAI() {
	    Queen q = new Queen(this.color, this.col, this.row, false); // không load ảnh
	    q.hasMoved = this.hasMoved;
	    q.preCol = this.preCol;
	    q.preRow = this.preRow;
	    return q;
	}

}
