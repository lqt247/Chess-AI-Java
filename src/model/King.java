package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ui.GamePanel;
import utils.ImageLoader;

public class King extends Pieces {
	public King(int color, int col, int row) {
		super(color, col, row);
		if (color == GamePanel.WHITE) {
			image = ImageLoader.load("/asset_pieces/w-king-pieces.png");
		} else {
			image = ImageLoader.load("/asset_pieces/b-king-pieces.png");

		}

	}

    public King(int color, int col, int row, boolean loadImage) {
        super(color, col, row);
        if (!loadImage) return;
        if (color == GamePanel.WHITE)
            image = ImageLoader.load("/asset_pieces/w-king-pieces.png");
        else
            image = ImageLoader.load("/asset_pieces/b-king-pieces.png");
    }
	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    if (!isWithInBoard(targetCol, targetRow)) return false;

	    int dCol = Math.abs(targetCol - col);
	    int dRow = Math.abs(targetRow - row);

	    // ===== ĐI 1 Ô =====
	    if (dCol <= 1 && dRow <= 1)
	        return !isAllyPiece(targetCol, targetRow);

	    // ===== NHẬP THÀNH =====
	    if (!hasMoved && dRow == 0 && dCol == 2) {
	        int rookCol = (targetCol > col) ? 7 : 0;
	        Pieces rook = getPiecesAt(rookCol, row);

	        if (rook instanceof Rook && !rook.hasMoved) {
	            int step = (targetCol > col) ? 1 : -1;
	            for (int c = col + step; c != rookCol; c += step) {
	                if (getPiecesAt(c, row) != null) return false;
	            }
	            return true;
	        }
	    }

	    return false;
	}
	@Override
	public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
	    int dc = targetCol - col;
	    int dr = targetRow - row;

	    if (Math.abs(dc) > 1 || Math.abs(dr) > 1) return false;

	    Pieces target = getPiecesAtSim(board, targetCol, targetRow);
	    return target == null || target.color != this.color;
	}







    @Override
    public Pieces copy() {
        King k = new King(color, col, row, false);
        k.hasMoved = this.hasMoved;
        return k;
    }

	


}
