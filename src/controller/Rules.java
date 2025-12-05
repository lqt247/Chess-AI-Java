package controller;

import java.util.ArrayList;
import model.*;

public class Rules {

    // ===============================
    // TÌM KING
    // ===============================
    public static Pieces findKing(ArrayList<Pieces> pieces, int color) {
        for (Pieces p : pieces) {
            if (p instanceof King && p.color == color)
                return p;
        }
        return null;
    }

    // ===============================
    // KIỂM TRA CHIẾU
    // ===============================
    public static boolean isKingInCheck(ArrayList<Pieces> board, int color) {
        Pieces king = findKing(board, color);
        if (king == null) return false;

        for (Pieces p : board) {
            if (p.color != color && p.canMoveSim(board, king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    // ===============================
    // LẤY TOÀN BỘ NƯỚC HỢP LỆ
    // ===============================
    public static ArrayList<int[]> getLegalMoves(ArrayList<Pieces> board, int color) {
        ArrayList<int[]> legal = new ArrayList<>();

        for (Pieces p : board) {
            if (p.color != color) continue;

            // Giới hạn simulate theo 8x8 board
            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    if (c == p.col && r == p.row) continue;

                    if (p.canMoveSim(board, c, r)) {
                        int[] move = {p.col, p.row, c, r};
                        if (isLegalAfterMove(board, move, color)) {
                            legal.add(move);
                        }
                    }
                }
            }
        }

        return legal;
    }

    // ===============================
    // GIẢ LẬP NƯỚC ĐI → KIỂM TRA TỰ CHIẾU
    // ===============================
    public static boolean isLegalAfterMove(ArrayList<Pieces> board, int[] move, int color) {
        int fc = move[0], fr = move[1];
        int tc = move[2], tr = move[3];

        ArrayList<Pieces> clone = cloneBoard(board);

        Pieces moving = null;
        for (Pieces p : clone) {
            if (p.col == fc && p.row == fr && p.color == color) {
                moving = p;
                break;
            }
        }
        if (moving == null) return false;

        // Xử lý ăn quân
        for (int i = 0; i < clone.size(); i++) {
            Pieces p = clone.get(i);
            if (p.col == tc && p.row == tr) {
                if (p instanceof King) return false;
                if (p.color != color) {
                    clone.remove(i);
                    break;
                }
            }
        }

        moving.col = tc;
        moving.row = tr;

        return !isKingInCheck(clone, color);
    }

    // ===============================
    // CHECKMATE
    // ===============================
    public static boolean isCheckmate(ArrayList<Pieces> pieces, int color) {
        if (!isKingInCheck(pieces, color)) return false;
        return getLegalMoves(pieces, color).isEmpty();
    }

    // ===============================
    // CLONE BOARD CHUẨN
    // ===============================
    public static ArrayList<Pieces> cloneBoard(ArrayList<Pieces> pieces) {
        ArrayList<Pieces> clone = new ArrayList<>();
        for (Pieces p : pieces) {
            Pieces c = p.copy();
            c.col = p.col;
            c.row = p.row;
            c.hasMoved = p.hasMoved;
            c.preCol = p.preCol;
            c.preRow = p.preRow;
            clone.add(c);
        }
        return clone;
    }
}
