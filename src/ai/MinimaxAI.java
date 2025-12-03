package ai;

import model.Pieces;
import java.util.ArrayList;

public class MinimaxAI implements AI {
    private int depth;
	private int color;

    public MinimaxAI(int color, int depth) {
        super();
        this.depth = depth;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> pieces) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (Pieces p : pieces) {
            if (p.color != this.color) continue; // chỉ xét quân AI
            ArrayList<int[]> moves = p.getValidMoves(); // hoặc simulateValidMoves
            for (int[] move : moves) {
                Pieces target = getPieceAt(move[0], move[1]);
                makeMove(p, move[0], move[1]);
                int score = minimax(pieces, depth - 1, false);
                undoMove(p, move[0], move[1], target);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new int[]{p.col, p.row, move[0], move[1], pieces.indexOf(p)};
                }
            }
        }

        return bestMove;
    }

    private Pieces getPieceAt(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	private int minimax(ArrayList<Pieces> pieces, int depth, boolean maxTurn) {
        if (depth == 0) return evaluateBoard(pieces);

        if (maxTurn) {
            int maxEval = Integer.MIN_VALUE;
            for (Pieces p : pieces) {
                if (p.color != this.color) continue;
                for (int[] move : p.getValidMoves()) {
                    Pieces target = getPieceAt(move[0], move[1]);
                    makeMove(p, move[0], move[1]);
                    int eval = minimax(pieces, depth - 1, false);
                    undoMove(p, move[0], move[1], target);
                    maxEval = Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Pieces p : pieces) {
                if (p.color == this.color) continue;
                for (int[] move : p.getValidMoves()) {
                    Pieces target = getPieceAt(move[0], move[1]);
                    makeMove(p, move[0], move[1]);
                    int eval = minimax(pieces, depth - 1, true);
                    undoMove(p, move[0], move[1], target);
                    minEval = Math.min(minEval, eval);
                }
            }
            return minEval;
        }
    }

    private int evaluateBoard(ArrayList<Pieces> pieces) {
        int score = 0;
        for (Pieces p : pieces) {
            score += (p.color == this.color ? 1 : -1) * p.getValue();
        }
        return score;
    }

    private void makeMove(Pieces p, int col, int row) { /* update bàn cờ */ }
    private void undoMove(Pieces p, int col, int row, Pieces target) { /* quay lại */ }
}
