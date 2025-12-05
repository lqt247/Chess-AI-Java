package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    public Knight(int color, int col, int row, boolean loadImage) {
        super(color, col, row);
        if (!loadImage) return;
        if (color == GamePanel.WHITE)
            image = ImageLoader.load("/accet_pieces/w-knight-pieces.png");
        else
            image = ImageLoader.load("/accet_pieces/b-knight-pieces.png");
    }
	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    int diffCol = Math.abs(targetCol - col);
	    int diffRow = Math.abs(targetRow - row);

	    // kiểm tra chữ L
	    if ((diffCol == 2 && diffRow == 1) || (diffCol == 1 && diffRow == 2)) {
	        Pieces target = getPiecesAt(targetCol, targetRow);
	        return target == null || target.color != this.color;
	    }
	    return false;
	}
	@Override
	public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
	    int dc = Math.abs(targetCol - col);
	    int dr = Math.abs(targetRow - row);
	    if (!((dc == 2 && dr == 1) || (dc == 1 && dr == 2))) return false;

	    Pieces target = getPiecesAtSim(board, targetCol, targetRow);
	    return target == null || target.color != color;
	}

	
	
	   @Override
	    public Pieces copy() {
	        Knight k = new Knight(color, col, row, false);
	        k.hasMoved = this.hasMoved;
	        return k;
	    }

}
