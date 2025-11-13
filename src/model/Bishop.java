package model;

import java.awt.image.BufferedImage;

import ui.GamePanel;
import utils.ImageLoader;

public class Bishop extends Pieces {
	public Bishop(int color, int col, int row) {
		super(color, col, row);		
		if(color == GamePanel.WHITE) 
		
		{
			image = ImageLoader.load("/accet_pieces/w-bishop-pieces.png");
		}
		else {
			image = ImageLoader.load("/accet_pieces/b-bishop-pieces.png");
			}
		} 
	
	@Override
	public boolean canMove(int targetCol, int targetRow) {
	    if (!isWithInBoard(targetCol, targetRow)) return false;
	    if (targetCol == col && targetRow == row) return false;

	    int dCol = targetCol - col;
	    int dRow = targetRow - row;
	    if (Math.abs(dCol) != Math.abs(dRow)) return false;

	    int stepCol = (dCol > 0) ? 1 : -1;
	    int stepRow = (dRow > 0) ? 1 : -1;

	    int c = col + stepCol;
	    int r = row + stepRow;
	    while (c != targetCol) {
	        if (getPieceAt(c, r) != null) return false;
	        c += stepCol;
	        r += stepRow;
	    }

	    return !isAllyPiece(targetCol, targetRow);
	}


}
