package ai;

import java.util.ArrayList;
import java.util.List;

import model.*;
import controller.Rules;

public class MinimaxAI implements AI {

    private int aiColor;
    private int maxDepth;
    private List<String> history;

    public MinimaxAI(int aiColor, int depth) {
        this.aiColor = aiColor;
        this.maxDepth = depth;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> board, List<String> history) {
        this.history = history; // lưu history để phạt lặp
        ArrayList<Pieces> clone = cloneBoard(board);
        MoveScore best = minimax(clone, maxDepth, aiColor, true);
        return best != null ? best.move : null;
    }

    private MoveScore minimax(ArrayList<Pieces> board, int depth, int turnColor, boolean isMax) {

        ArrayList<int[]> moves = Rules.getLegalMoves(board, turnColor);

        if (depth == 0 || moves.isEmpty()) {
            int val = Heuristic.evaluate(board, aiColor);
            return new MoveScore(null, val);
        }

        MoveScore best = null;

        for (int[] move : moves) {

            ArrayList<Pieces> nextBoard = cloneBoard(board);
            applyMove(nextBoard, move);

            int nextTurn = (turnColor == 1) ? 0 : 1;
            MoveScore child = minimax(nextBoard, depth - 1, nextTurn, !isMax);

            // PHẠT LẶP NƯỚC 
            Pieces p = getPieceAt(board, move[0], move[1]);
            String moveStr = encodeMove(p, move);

            int penalty = 0;
            if (isRepeatMove(history, moveStr)) {
                penalty = 5000;
            }

            int finalScore = child.score - penalty;
            // ----------------------------------------------------------------------

            if (best == null) {
                best = new MoveScore(move, finalScore);
            } else {
                if (isMax && finalScore > best.score) {
                    best = new MoveScore(move, finalScore);
                }
                if (!isMax && finalScore < best.score) {
                    best = new MoveScore(move, finalScore);
                }
            }
        }

        return best;
    }

    // CÁC HÀM PHỤ 

    private boolean isRepeatMove(List<String> history, String move) {
        if (history == null) return false;
        int n = history.size();
        if (n < 2) return false;
        return move.equals(history.get(n - 2));
    }

    private String encodeMove(Pieces p, int[] mv) {
        if (p == null) return "";
        String colorName = (p.color == 1) ? "Trắng" : "Đen";
        String pieceName = p.getClass().getSimpleName();
        return pieceName + " (" + colorName + "): "
             + toChess(mv[0], mv[1]) + " -> " + toChess(mv[2], mv[3]);
    }

    private String toChess(int c, int r) {
        return "" + (char)('a' + c) + (8 - r);
    }

    private void applyMove(ArrayList<Pieces> board, int[] mv) {
        Pieces p = getPieceAt(board, mv[0], mv[1]);
        if (p == null) return;

        Pieces captured = getPieceAt(board, mv[2], mv[3]);

        p.col = mv[2];
        p.row = mv[3];

        if (captured != null)
            board.remove(captured);
    }

    private Pieces getPieceAt(ArrayList<Pieces> board, int col, int row) {
        for (Pieces p : board)
            if (p.col == col && p.row == row)
                return p;
        return null;
    }

    private ArrayList<Pieces> cloneBoard(ArrayList<Pieces> board) {
        ArrayList<Pieces> clone = new ArrayList<>();
        for (Pieces p : board)
            clone.add(p.copyForAI());
        return clone;
    }

    private static class MoveScore {
        int[] move;
        int score;
        MoveScore(int[] m, int s) {
            move = m;
            score = s;
        }
    }
}
