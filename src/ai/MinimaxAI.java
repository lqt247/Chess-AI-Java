package ai;

import java.util.ArrayList;
import controller.Rules;
import model.Pieces;

public class MinimaxAI implements AI { // ✅ implement AI

    private int aiColor;
    private int maxDepth;

    public MinimaxAI(int aiColor, int depth) {
        this.aiColor = aiColor;
        this.maxDepth = depth;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> board) {
        MoveScore best = minimax(board, maxDepth, aiColor, true);
        return best.move;
    }

    private MoveScore minimax(ArrayList<Pieces> board, int depth, int turnColor, boolean isMaximizing) {
        ArrayList<int[]> legalMoves = Rules.getLegalMoves(board, turnColor);

        if (depth == 0 || legalMoves.isEmpty()) {
            int score = ai.Heuristic.evaluate(board, aiColor);
            return new MoveScore(null, score);
        }

        MoveScore best = null;

        for (int[] move : legalMoves) {
            ArrayList<Pieces> clone = cloneBoard(board);

            // Thực hiện nước đi trên clone
            Pieces moving = clone.get(move[4]);
            for (int i = 0; i < clone.size(); i++) {
                Pieces p = clone.get(i);
                if (p.col == move[2] && p.row == move[3] && p.color != moving.color) {
                    clone.remove(i);
                    break;
                }
            }

            moving.col = move[2];
            moving.row = move[3];

            int nextTurn = (turnColor == 1) ? 0 : 1;
            MoveScore result = minimax(clone, depth - 1, nextTurn, !isMaximizing);

            if (best == null) best = new MoveScore(move, result.score);
            else {
                if (isMaximizing && result.score > best.score) best = new MoveScore(move, result.score);
                if (!isMaximizing && result.score < best.score) best = new MoveScore(move, result.score);
            }
        }

        if (best == null) return new MoveScore(null, ai.Heuristic.evaluate(board, aiColor));
        return best;
    }

    private ArrayList<Pieces> cloneBoard(ArrayList<Pieces> board) {
        ArrayList<Pieces> clone = new ArrayList<>();
        for (Pieces p : board) clone.add(p.copy());
        return clone;
    }

    private static class MoveScore {
        int[] move;
        int score;
        MoveScore(int[] m, int s) { move = m; score = s; }
    }
}
