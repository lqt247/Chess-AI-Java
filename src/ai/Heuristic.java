package ai;

//Đánh giá bàn cờ (evaluation function)


import java.util.ArrayList;
import model.*;

public class Heuristic {

    // ✅ ĐÁNH GIÁ BÀN CỜ
    public static int evaluate(ArrayList<Pieces> board, int aiColor) {
        int score = 0;

        for (Pieces p : board) {
            int value = getPieceValue(p);

            if (p.color == aiColor)
                score += value;
            else
                score -= value;
        }

        return score;
    }

    // ✅ GIÁ TRỊ TỪNG QUÂN
    private static int getPieceValue(Pieces p) {
        if (p instanceof Pawn) return 100;
        if (p instanceof Knight) return 320;
        if (p instanceof Bishop) return 330;
        if (p instanceof Rook) return 500;
        if (p instanceof Queen) return 900;
        if (p instanceof King) return 20000;
        return 0;
    }
}

