package ai;

import java.util.ArrayList;
import java.util.List;
import controller.Rules;
import model.*;

public class AlphaBetaAI implements AI {

    private int aiColor;
    private int maxDepth;

    public AlphaBetaAI(int aiColor, int depth) {
        this.aiColor = aiColor;
        this.maxDepth = depth;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> board, List<String> history) {
        MoveScore best = alphaBetaRoot(board, maxDepth);
        return best != null ? best.move : null;
    }

    // ================= ROOT =================

    private MoveScore alphaBetaRoot(ArrayList<Pieces> board, int depth) {
        ArrayList<int[]> moves = Rules.getLegalMoves(board, aiColor);
        if (moves.isEmpty()) return null;

        int alpha = Integer.MIN_VALUE;
        int beta  = Integer.MAX_VALUE;

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int[] mv : moves) {
            MoveState st = applyMove(board, mv);
            int score = alphaBeta(board, depth - 1, alpha, beta, 1 - aiColor, false);
            undoMove(board, st);

            if (score > bestScore) {
                bestScore = score;
                bestMove = mv;
            }

            alpha = Math.max(alpha, score);
        }

        return new MoveScore(bestMove, bestScore);
    }

    // ================= ALPHA BETA =================

    private int alphaBeta(
            ArrayList<Pieces> board,
            int depth,
            int alpha,
            int beta,
            int turnColor,
            boolean maximizing
    ) {
        if (depth == 0) {
            return Heuristic.evaluate(board, aiColor);
        }

        ArrayList<int[]> moves = Rules.getLegalMoves(board, turnColor);

        // HẾT NƯỚC
        if (moves.isEmpty()) {
            if (Rules.isKingInCheck(board, turnColor)) {
                // bị chiếu bí
                return maximizing ? -100000 : 100000;
            }
            // stalemate
            return 0;
        }

        if (maximizing) {
            int best = Integer.MIN_VALUE;

            for (int[] mv : moves) {
                MoveState st = applyMove(board, mv);
                int val = alphaBeta(board, depth - 1, alpha, beta, 1 - turnColor, false);
                undoMove(board, st);

                best = Math.max(best, val);
                alpha = Math.max(alpha, val);

                if (alpha >= beta) break; // CẮT TỈA
            }

            return best;

        } else {
            int best = Integer.MAX_VALUE;

            for (int[] mv : moves) {
                MoveState st = applyMove(board, mv);
                int val = alphaBeta(board, depth - 1, alpha, beta, 1 - turnColor, true);
                undoMove(board, st);

                best = Math.min(best, val);
                beta = Math.min(beta, val);

                if (alpha >= beta) break; // CẮT TỈA
            }

            return best;
        }
    }

    // ================= APPLY / UNDO =================

    private static class MoveState {
        Pieces piece;
        int fromCol, fromRow;
        Pieces captured;
    }

    private MoveState applyMove(ArrayList<Pieces> board, int[] mv) {
        MoveState st = new MoveState();

        int fc = mv[0], fr = mv[1];
        int tc = mv[2], tr = mv[3];

        Pieces moving = null;
        for (Pieces p : board) {
            if (p.col == fc && p.row == fr) {
                moving = p;
                break;
            }
        }

        st.piece = moving;
        st.fromCol = fc;
        st.fromRow = fr;

        for (int i = board.size() - 1; i >= 0; i--) {
            Pieces p = board.get(i);
            if (p.col == tc && p.row == tr && p.color != moving.color) {
                st.captured = p;
                board.remove(i);
                break;
            }
        }

        moving.col = tc;
        moving.row = tr;

        return st;
    }

    private void undoMove(ArrayList<Pieces> board, MoveState st) {
        st.piece.col = st.fromCol;
        st.piece.row = st.fromRow;

        if (st.captured != null)
            board.add(st.captured);
    }

    // ================= DATA =================

    private static class MoveScore {
        int[] move;
        int score;
        MoveScore(int[] m, int s) {
            move = m;
            score = s;
        }
    }
}
