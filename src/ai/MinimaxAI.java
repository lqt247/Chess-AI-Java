package ai;

import java.util.ArrayList;
import model.*;
import controller.Rules;

public class MinimaxAI implements AI {

    private int aiColor;
    private int maxDepth;

    public MinimaxAI(int aiColor, int depth) {
        this.aiColor = aiColor;
        this.maxDepth = depth;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> board) {
        MoveScore best = minimax(board, maxDepth, aiColor, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return best.move;
    }

    private MoveScore minimax(ArrayList<Pieces> board, int depth, int turnColor, boolean isMaximizing, int alpha, int beta) {
        ArrayList<int[]> legalMoves = Rules.getLegalMoves(board, turnColor);

        if (depth == 0 || legalMoves.isEmpty()) {
            int score = Heuristic.evaluate(board, aiColor);
            return new MoveScore(null, score);
        }

        // Move ordering: ưu tiên ăn quân
        legalMoves.sort((a, b) -> {
            Pieces targetA = getPieceAt(board, a[2], a[3]);
            Pieces targetB = getPieceAt(board, b[2], b[3]);
            int valA = targetA != null ? Heuristic.getPieceValue(targetA) : 0;
            int valB = targetB != null ? Heuristic.getPieceValue(targetB) : 0;
            return Integer.compare(valB, valA);
        });

        MoveScore best = null;

        for (int[] move : legalMoves) {
            Pieces moving = getPieceAt(board, move[0], move[1]);
            if (moving == null) continue;

            // Lưu lại thông tin undo
            int fromCol = moving.col, fromRow = moving.row;
            Pieces captured = getPieceAt(board, move[2], move[3]);

            // Thực hiện move
            moving.col = move[2];
            moving.row = move[3];
            if (captured != null) board.remove(captured);

            // Đệ quy
            int nextTurn = (turnColor == 1) ? 0 : 1;
            MoveScore result = minimax(board, depth - 1, nextTurn, !isMaximizing, alpha, beta);

            // Undo move
            moving.col = fromCol;
            moving.row = fromRow;
            if (captured != null) board.add(captured);

            // Cập nhật best
            if (best == null) best = new MoveScore(move, result.score);
            else {
                if (isMaximizing && result.score > best.score) best = new MoveScore(move, result.score);
                if (!isMaximizing && result.score < best.score) best = new MoveScore(move, result.score);
            }

            // Alpha-Beta pruning
            if (isMaximizing) {
                alpha = Math.max(alpha, best.score);
                if (beta <= alpha) break;
            } else {
                beta = Math.min(beta, best.score);
                if (beta <= alpha) break;
            }
        }

        if (best == null) return new MoveScore(null, Heuristic.evaluate(board, aiColor));
        return best;
    }

    private Pieces getPieceAt(ArrayList<Pieces> board, int col, int row) {
        for (Pieces p : board) {
            if (p.col == col && p.row == row) return p;
        }
        return null;
    }

    private static class MoveScore {
        int[] move;
        int score;
        MoveScore(int[] m, int s) { move = m; score = s; }
    }
}
