package model;

import java.awt.image.BufferedImage;

import main.GameFrame;
import ui.GamePanel;
import utils.ImageLoader;

public class Pawn extends Pieces {

	public Pawn(int color, int col, int row) {
		super( color,col, row);

		if (color == GamePanel.WHITE) {
			image = ImageLoader.load("/accet_pieces/w-pawn-pieces.png");

		} else {
			image = ImageLoader.load("/accet_pieces/b-pawn-pieces.png");

		}
	}
	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    // Không đi ra ngoài board
	    if (!isWithInBoard(targetCol, targetRow)) return false;

	    int dir = (color == GamePanel.WHITE) ? -1 : 1; // WHITE đi lên, BLACK đi xuống
	    int startRow = (color == GamePanel.WHITE) ? 6 : 1;

	    // ---- Đi thẳng ----
	    if (targetCol == col) {
	        // 1 ô trước
	        if (targetRow == row + dir && getPiecesAt(targetCol, targetRow) == null)
	            return true;

	        // 2 ô đầu tiên
	        if (row == startRow && targetRow == row + 2*dir && 
	            getPiecesAt(targetCol, row + dir) == null &&
	            getPiecesAt(targetCol, targetRow) == null)
	            return true;
	    }

	    // ---- Ăn quân chéo ----
	    if ((targetCol == col + 1 || targetCol == col -1) &&
	        targetRow == row + dir) {
	        Pieces target = getPiecesAt(targetCol, targetRow);
	        if (target != null && target.color != this.color)
	            return true;
	    }

	    return false;
	}


	

}
