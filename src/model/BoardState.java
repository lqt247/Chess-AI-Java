package model;

import java.util.ArrayList;

public class BoardState {
    public Pieces[][] board; // board[col][row]
    public int currentTurn; // GamePanel.WHITE or BLACK

    public BoardState() {
        board = new Pieces[8][8];
        currentTurn = -1;
    }

    // build BoardState from GamePanel.pieces
    public static BoardState fromPieces(ArrayList<Pieces> pieces, int currentTurn) {
        BoardState s = new BoardState();
        s.currentTurn = currentTurn;
        for (Pieces p : pieces) {
            if (p != null && p.col >=0 && p.col < 8 && p.row >=0 && p.row < 8) {
                s.board[p.col][p.row] = p.copy(); // use copy() (you'll add)
            }
        }
        return s;
    }

    // shallow copy -> deep copy by calling copy() of pieces
    public BoardState copy() {
        BoardState s = new BoardState();
        s.currentTurn = this.currentTurn;
        for (int c=0;c<8;c++) for (int r=0;r<8;r++) {
            if (this.board[c][r] != null) s.board[c][r] = this.board[c][r].copy();
        }
        return s;
    }

    public Pieces getPiece(int c, int r) {
        if (c < 0 || c > 7 || r < 0 || r > 7) return null;
        return board[c][r];
    }

    public void setPiece(int c, int r, Pieces p) {
        if (c < 0 || c > 7 || r < 0 || r > 7) return;
        board[c][r] = p;
        if (p != null) { p.col = c; p.row = r; }
    }

    public void makeMove(Move m) {
        Pieces p = getPiece(m.fromCol, m.fromRow);
        if (p == null) return;
        Pieces cap = getPiece(m.toCol, m.toRow);
        // move
        setPiece(m.toCol, m.toRow, p.copy()); // ensure piece instance in boardstate is copy
        setPiece(m.fromCol, m.fromRow, null);
        // adjust piece properties
        p = getPiece(m.toCol, m.toRow);
        p.hasMoved = true;
        // promotion: handle externally if needed
    }

    // helper to list all pieces as arraylist (useful for interoperability)
    public ArrayList<Pieces> asList() {
        ArrayList<Pieces> list = new ArrayList<>();
        for (int c=0;c<8;c++) for (int r=0;r<8;r++) if (board[c][r] != null) list.add(board[c][r]);
        return list;
    }
}
