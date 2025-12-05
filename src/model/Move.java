package model;

public class Move {
    public int fromCol, fromRow;
    public int toCol, toRow;
    public Pieces moved;      
    public Pieces captured;   

    public Move(int fC, int fR, int tC, int tR) {
        this.fromCol = fC;
        this.fromRow = fR;
        this.toCol = tC;
        this.toRow = tR;
    }

    public String toString() {
        return "[" + fromCol + "," + fromRow + " -> " + toCol + "," + toRow + "]";
    }
}
