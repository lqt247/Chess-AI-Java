package ai;

import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import controller.Rules;

public class SimpleAI implements AI {
    private int color;
    private Random random = new Random();

    public SimpleAI(int color) {
        this.color = color;
    }
    private java.util.HashMap<Long, Integer> repetitionMap = new java.util.HashMap<>();

    @Override
    public int[] chooseMove(ArrayList<Pieces> pieces, List<String> history) {
        // Lấy toàn bộ nước đi hợp lệ (đã bao gồm thoát chiếu)
        ArrayList<int[]> legalMoves = Rules.getLegalMoves(pieces, color);

        if (legalMoves.isEmpty()) return null; // HẾT NƯỚC → THUA

        ArrayList<int[]> bestMoves = new ArrayList<>();

        for (int[] move : legalMoves) {
            ArrayList<Pieces> testBoard = Rules.cloneBoard(pieces);

            int fromC = move[0];
            int fromR = move[1];
            int toC   = move[2];
            int toR   = move[3];

            Pieces moving = null;

            // tìm quân theo tọa độ
            for (Pieces p : testBoard) {
                if (p.col == fromC && p.row == fromR && p.color == color) {
                    moving = p;
                    break;
                }
            }

            if (moving == null) continue;

            //  ăn quân nếu có
            for (int i = 0; i < testBoard.size(); i++) {
                Pieces t = testBoard.get(i);
                if (t.col == toC && t.row == toR && t.color != color) {
                    testBoard.remove(i);
                    break;
                }
            }

            moving.col = toC;
            moving.row = toR;

            //  Ưu tiên nước chiếu lại đối phương
            if (Rules.isKingInCheck(testBoard, 1 - color)) {
                bestMoves.add(move);
            }
        }

        if (!bestMoves.isEmpty()) {
            return bestMoves.get(random.nextInt(bestMoves.size()));
        }

        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}
