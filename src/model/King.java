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

	    int dCol = targetCol - col;
	    int dRow = Math.abs(targetRow - row);

	    // Di chuyển 1 ô bình thường
	    if (Math.abs(dCol) <= 1 && dRow <= 1)
	        return !isAllyPiece(targetCol, targetRow);

	    // Nhập thành: chỉ được đi 2 ô sang trái hoặc phải, cùng hàng, chưa di chuyển, ô giữa trống
	    if (!hasMoved && dRow == 0 && Math.abs(dCol) == 2) {
	        int rookCol = (dCol > 0) ? 7 : 0;
	        Pieces rook = getPiecesAt(rookCol, row);
	        if (rook instanceof Rook && !((Rook) rook).hasMoved) {
	            int step = (dCol > 0) ? 1 : -1;
	            for (int c = col + step; c != rookCol; c += step) {
	                if (getPiecesAt(c, row) != null) return false;
	            }
	            return true;
	        }
	    }

	    return false;
	}


}
