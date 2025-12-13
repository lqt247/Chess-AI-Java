package ai;

import java.util.ArrayList;
import model.*;
import controller.Rules;

public class Heuristic {

    // Hàm đánh giá tổng
    public static int evaluate(ArrayList<Pieces> board, int aiColor) {
        int score = 0;
        int phase = getGamePhase(board); // 0: khai cuộc, 1: trung cuộc, 2: tàn cuộc

        for (Pieces p : board) {

            int pieceScore = 0;

            // Giá trị cơ bản của quân
            pieceScore += getPieceValue(p);

            // Thưởng vị trí
            pieceScore += getPositionalBonus(p, phase);

            // Thưởng độ cơ động
            pieceScore += getMobilityBonus(p, board, phase);

            // Phạt vua không an toàn
            if (p instanceof King) {
                pieceScore -= getKingDanger(p, board, phase);
            }

            // Phạt quân bị treo
            pieceScore -= getHangingPenalty(p, board);

            // Phạt Hậu ra sớm
            if (p instanceof Queen && phase == 0) {
                pieceScore -= 180;
            }

            // Cộng / trừ theo màu
            if (p.color == aiColor)
                score += pieceScore;
            else
                score -= pieceScore;
        }

        return score;
    }

    // Xác định giai đoạn trận đấu
    private static int getGamePhase(ArrayList<Pieces> board) {
        int material = 0;

        for (Pieces p : board) {
            if (!(p instanceof Pawn) && !(p instanceof King)) {
                material += getPieceValue(p);
            }
        }

        if (material > 4000) return 0; // khai cuộc
        if (material > 2000) return 1; // trung cuộc
        return 2;                      // tàn cuộc
    }

    // Giá trị quân cờ
    public static int getPieceValue(Pieces p) {
        if (p instanceof Pawn)   return 100;
        if (p instanceof Knight) return 320;
        if (p instanceof Bishop) return 330;
        if (p instanceof Rook)   return 500;
        if (p instanceof Queen)  return 900;
        if (p instanceof King)   return 20000;
        return 0;
    }

    // Thưởng vị trí theo loại quân
    private static int getPositionalBonus(Pieces p, int phase) {
        int bonus = 0;
        int col = p.col;
        int row = p.row;

        // Thưởng kiểm soát trung tâm
        if (col >= 2 && col <= 5 && row >= 2 && row <= 5) {
            bonus += 20;
        }

        // Pawn càng tiến càng tốt
        if (p instanceof Pawn) {
            if (p.color == 1) bonus += (6 - row) * 8;
            else bonus += row * 8;
        }

        // Thưởng phát triển mã và tượng
        if (p instanceof Knight || p instanceof Bishop) {
            if (phase == 0) bonus += 40;
            if (phase == 1) bonus += 20;
        }

        // Tàn cuộc: vua nên tiến lên
        if (p instanceof King && phase == 2) {
            bonus += 30;
        }

        return bonus;
    }

    // Thưởng độ cơ động
    private static int getMobilityBonus(Pieces p, ArrayList<Pieces> board, int phase) {
        int moves = p.getValidMoves(board).size();

        // Nerf hậu trong khai cuộc
        if (p instanceof Queen && phase == 0) {
            moves /= 2;
        }

        return moves * 5;
    }

    // Phạt vua bị đe dọa
    private static int getKingDanger(Pieces king, ArrayList<Pieces> board, int phase) {
        int danger = 0;

        for (Pieces p : board) {
            if (p.color != king.color && p.canMoveSim(board, king.col, king.row)) {
                danger += getPieceValue(p) / 10;
            }
        }

        // Khai cuộc mà vua đi sớm là rất tệ
        if (phase == 0 && king.hasMoved) {
            danger += 100;
        }

        return danger;
    }

    // Phạt quân bị treo (không được bảo vệ)
    private static int getHangingPenalty(Pieces p, ArrayList<Pieces> board) {
        boolean attacked = false;
        boolean defended = false;

        for (Pieces other : board) {
            if (other.color != p.color &&
                other.canMoveSim(board, p.col, p.row)) {
                attacked = true;
            }
            if (other.color == p.color &&
                other.canMoveSim(board, p.col, p.row)) {
                defended = true;
            }
        }

        if (attacked && !defended) {
            return getPieceValue(p) / 2;
        }

        return 0;
    }
}
