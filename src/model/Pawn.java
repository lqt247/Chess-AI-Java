package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.GameFrame;
import ui.GamePanel;
import utils.ImageLoader;

public class Pawn extends Pieces {

	public Pawn(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = ImageLoader.load("/asset_pieces/w-pawn-pieces.png");

		} else {
			image = ImageLoader.load("/asset_pieces/b-pawn-pieces.png");

		}
	}	
	public Pawn(int color, int col, int row, boolean loadImage) {
	    super(color, col, row);

	    if (!loadImage) return; // AI clone → không load ảnh

	    if (color == GamePanel.WHITE) {
	        image = ImageLoader.load("/asset_pieces/w-pawn-pieces.png");
	    } else {
	        image = ImageLoader.load("/asset_pieces/b-pawn-pieces.png");
	    }
	}

	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	@Override
	public boolean canMove(int targetCol, int targetRow) {
		// Không đi ra ngoài board
		if (!isWithInBoard(targetCol, targetRow))
			return false;

		int dir = (color == GamePanel.WHITE) ? -1 : 1; // WHITE đi lên, BLACK đi xuống
		int startRow = (color == GamePanel.WHITE) ? 6 : 1;

		// ---- Đi thẳng ----
		if (targetCol == col) {
			// 1 ô trước
			if (targetRow == row + dir && getPiecesAt(targetCol, targetRow) == null)
				return true;

			// 2 ô đầu tiên
			if (row == startRow && targetRow == row + 2 * dir && getPiecesAt(targetCol, row + dir) == null
					&& getPiecesAt(targetCol, targetRow) == null)
				return true;
		}

		// ---- Ăn quân chéo ----
		if ((targetCol == col + 1 || targetCol == col - 1) && targetRow == row + dir) {
			Pieces target = getPiecesAt(targetCol, targetRow);
			if (target != null && target.color != this.color)
				return true;
		}

		return false;
	}

	@Override
	
	public Pieces copy() {
	    Pawn p = new Pawn(this.color, this.col, this.row, false); // không load ảnh
	    p.hasMoved = this.hasMoved;
	    return p;
	}


	@Override
	public boolean canMoveSim(ArrayList<Pieces> board, int targetCol, int targetRow) {
	    int dir = (color == GamePanel.WHITE) ? -1 : 1;
	    int startRow = (color == GamePanel.WHITE) ? 6 : 1;

	    int dc = targetCol - col;
	    int dr = targetRow - row;

	    // Ăn chéo
	    if (Math.abs(dc) == 1 && dr == dir) {
	        Pieces target = getPiecesAtSim(board, targetCol, targetRow);
	        return target != null && target.color != this.color;
	    }

	    // Đi thẳng
	    if (dc == 0) {
	        if (dr == dir && isEmptySim(board, targetCol, targetRow)) return true;
	        if (dr == 2*dir && row == startRow && isEmptySim(board, targetCol, row + dir) && isEmptySim(board, targetCol, targetRow)) return true;
	    }

	    return false;
	}




}
