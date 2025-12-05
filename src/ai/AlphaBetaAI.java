package ai;

import java.util.ArrayList;
import model.*;
import controller.Rules;
import javax.swing.SwingUtilities;

public class AlphaBetaAI implements AI {

    private int aiColor;
    private int maxDepth;

    public AlphaBetaAI(int aiColor, int depth) {
        this.aiColor = aiColor;
        this.maxDepth = depth;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> board) {
        // Thread-safe: AI thao tác trên clone board
        ArrayList<Pieces> clone = cloneBoardForAI(board);
        MoveScore best = alphaBeta(clone, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, aiColor, true);
        return best.move;
    }

    // ===== Alpha-Beta với move ordering và clone board =====
    private MoveScore alphaBeta(ArrayList<Pieces> board, int depth, int alpha, int beta, int turnColor, boolean isMaximizing) {
        ArrayList<int[]> legalMoves = Rules.getLegalMoves(board, turnColor);
        if (depth == 0 || legalMoves.isEmpty()) {
            return new MoveScore(null, Heuristic.evaluate(board, aiColor));
        }

        // --- Move ordering: ưu tiên ăn quân, chiếm trung tâm, chiếu ---
        legalMoves.sort((a, b) -> getMoveHeuristic(board, b) - getMoveHeuristic(board, a));

        MoveScore best = null;

        for (int[] move : legalMoves) {
            ArrayList<Pieces> clone = cloneBoardForAI(board);

            Pieces moving = getPieceAt(clone, move[0], move[1]);
            if (moving == null) continue;

            Pieces captured = getPieceAt(clone, move[2], move[3]);
            moving.col = move[2];
            moving.row = move[3];
            if (captured != null) clone.remove(captured);

            int nextTurn = (turnColor == 1) ? 0 : 1;
            MoveScore result = alphaBeta(clone, depth - 1, alpha, beta, nextTurn, !isMaximizing);

            if (best == null) best = new MoveScore(move, result.score);
            else {
                if (isMaximizing && result.score > best.score) best = new MoveScore(move, result.score);
                if (!isMaximizing && result.score < best.score) best = new MoveScore(move, result.score);
            }

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

    // ===== Heuristic cho move ordering: ăn quân, trung tâm =====
    private int getMoveHeuristic(ArrayList<Pieces> board, int[] move) {
        int score = 0;
        Pieces target = getPieceAt(board, move[2], move[3]);
        if (target != null) score += Heuristic.getPieceValue(target) * 10;
        score += 4 - Math.abs(3.5 - move[2]) + 4 - Math.abs(3.5 - move[3]); // trung tâm
        return score;
    }

    // ===== Lấy quân tại vị trí =====
    private Pieces getPieceAt(ArrayList<Pieces> board, int col, int row) {
        for (Pieces p : board) {
            if (p.col == col && p.row == row) return p;
        }
        return null;
    }

    // ===== Clone board lightweight cho AI =====
    private ArrayList<Pieces> cloneBoardForAI(ArrayList<Pieces> board) {
        ArrayList<Pieces> clone = new ArrayList<>();
        for (Pieces p : board) clone.add(p.copyForAI());
        return clone;
    }

    // ===== MoveScore nội bộ =====
    private static class MoveScore {
        int[] move;
        int score;
        MoveScore(int[] m, int s) { move = m; score = s; }
    }

    // ===== Chạy AI trên thread riêng để GUI không lag =====
    public void makeAIMove(ArrayList<Pieces> board, Runnable callback) {
        new Thread(() -> {
            int[] move = chooseMove(board);
            SwingUtilities.invokeLater(() -> {
                // callback để cập nhật bàn cờ và repaint
                if (callback != null) callback.run();
            });
        }).start();
    }
}
