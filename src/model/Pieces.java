package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import ui.GamePanel;

public class Pieces {
	public BufferedImage image;
	public int col, row, preCol, preRow;
	public int color;
	public int x, y;

	public Pieces(int color, int col, int row) {
		this.color = color;
		this.col = col;
		this.row = row;
		x = getX(col);
		y = getY(row);
	}
	public boolean hasMoved = false;

	protected Pieces getPiecesAt(int col, int row) {
		for (Pieces p : GamePanel.pieces)
			if (p.col == col && p.row == row)
				return p;
		return null;
	}
	public Pieces clonePiece() {
	    try {
	        return (Pieces) this.clone();
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	
	public int getX(int col) {
		return Board.offsetX + col * Board.SQUARE_SIZE;
	}

	public int getY(int row) {
		return Board.offsetY + row * Board.SQUARE_SIZE;
	}

	public int getCol(int x) {
		return (x - Board.offsetX) / Board.SQUARE_SIZE;
	}

	public int getRow(int y) {
		return (y - Board.offsetY) / Board.SQUARE_SIZE;
	}

	public boolean isWithInBoard(int targetCol, int targetRow) {
		if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}
		return false;
	}

	// NƯỚC MÀ QUÂN CÓ THỂ ĐI
	public boolean canMove(int targetCol, int targetRow) {
		if (!isWithInBoard(targetCol, targetRow))
			return false;
		if (targetCol == col && targetRow == row)
			return false; // không di chuyển tại chỗ
		return false;
	}
	// CHECK ĐỒNG MINH
	protected boolean isAllyPiece(int targetCol, int targetRow) {
		Pieces target = getPiecesAt(targetCol, targetRow);
		return target != null && target.color == this.color;
	}



	public void draw(Graphics2D g2) {
		if (image != null)
			g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	public ArrayList<int[]> getValidMoves() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}
}
