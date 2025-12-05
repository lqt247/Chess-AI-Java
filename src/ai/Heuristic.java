package ai;

import java.util.ArrayList;
import model.*;

public class Heuristic {

    // ✅ HÀM ĐÁNH GIÁ TOÀN BÀN CỜ
    public static int evaluate(ArrayList<Pieces> board, int aiColor) {
        int score = 0;

        for (Pieces p : board) {
            int material = getPieceValue(p);              // Giá trị quân
            int positional = getPositionalValue(p);       // Vị trí
            int mobility = getMobility(p, board);         // Số nước đi hợp lệ
            int safety = (p instanceof King) ? -getKingDanger(p, board) : 0; // An toàn vua
            int threat = getThreatScore(p, board);       // Đe doạ đối phương / bị đe doạ

            int pieceScore = material + positional + mobility + safety + threat;

            if (p.color == aiColor)
                score += pieceScore;
            else
                score -= pieceScore;
        }

        //  Thêm một chút random ở bước đầu để tránh đi y hệt mỗi trận
        double randomness = Math.random() * 5;
        score += (aiColor == 1 ? 1 : -1) * randomness;

        return score;
    }

    // ===== GIÁ TRỊ QUÂN CƠ BẢN =====
    static int getPieceValue(Pieces p) {
        if (p instanceof Pawn) return 100;
        if (p instanceof Knight) return 320;
        if (p instanceof Bishop) return 330;
        if (p instanceof Rook) return 500;
        if (p instanceof Queen) return 900;
        if (p instanceof King) return 20000;
        return 0;
    }

    // ===== GIÁ TRỊ VỊ TRÍ (trung tâm, mở file, connected pawn) =====
    private static int getPositionalValue(Pieces p) {
        int col = p.col;
        int row = p.row;

        // Trung tâm bàn cờ
        int centerBonus = 0;
        if (col >= 2 && col <= 5 && row >= 2 && row <= 5) centerBonus = 20;

        // Pawn càng tiến lên càng tốt
        int pawnBonus = 0;
        if (p instanceof Pawn) {
            if (p.color == 1) pawnBonus = (6 - row) * 10;  // trắng tiến lên rank thấp
            else pawnBonus = row * 10;                     // đen tiến lên rank cao
        }

        return centerBonus + pawnBonus;
    }

    // ===== MOBILITY: số nước đi hợp lệ =====
    private static int getMobility(Pieces p, ArrayList<Pieces> board) {
        ArrayList<int[]> moves = p.getValidMoves(board);
        return moves.size() * 5; // mỗi nước đi hợp lệ cộng 5 điểm
    }

    // ===== AN TOÀN VUA =====
    private static int getKingDanger(Pieces king, ArrayList<Pieces> board) {
        int danger = 0;
        for (Pieces p : board) {
            if (p.color != king.color && p.canMoveSim(board, king.col, king.row)) {
                danger += getPieceValue(p) / 10; // quân đe doạ vua → giảm score
            }
        }
        return danger;
    }

    // ===== ĐE DOẠ / BỊ ĐE DOẠ =====
    private static int getThreatScore(Pieces p, ArrayList<Pieces> board) {
        int score = 0;
        for (Pieces target : board) {
            if (target.color != p.color) {
                if (p.canMoveSim(board, target.col, target.row)) {
                    score += getPieceValue(target) / 5; // đe doạ quân đối phương
                }
                if (target.canMoveSim(board, p.col, p.row)) {
                    score -= getPieceValue(p) / 10; // bị đe doạ → trừ điểm
                }
            }
        }
        return score;
    }
}
