package ai;

import java.util.*;
import controller.Rules;
import model.*;


public class AlphaBetaAI implements AI {

    /** Màu của AI: 1 = trắng, 0 = đen */
    private final int aiColor;

 
    private final int maxDepth;


    private final HashMap<Long, TTEntry> tt = new HashMap<>();


    private final HashMap<Long, Integer> repetitionMap = new HashMap<>();


    private final long[][] zobristPieceSquare;

  
    private final long zobristSide;

  
    private final int[][] history;


    private static final int[] PIECE_VALUES = {
        100, 320, 330, 500, 900, 20000 
    };

    public AlphaBetaAI(int color, int depth) {
        this.aiColor = color;
        this.maxDepth = depth;

    
        this.zobristPieceSquare = new long[12][64];
        Random rnd = new Random(1234567); // seed cố định
        for (int i = 0; i < 12; i++)
            for (int j = 0; j < 64; j++)
                zobristPieceSquare[i][j] = rnd.nextLong();

        this.zobristSide = rnd.nextLong();

      
        this.history = new int[64][64];
    }


    @Override
    public int[] chooseMove(ArrayList<Pieces> board, List<String> history) {
        int[] best = null;
        tt.clear();

        for (int depth = 1; depth <= maxDepth; depth++) {
            MoveScore ms = alphaBetaRoot(board, depth, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
            if (ms != null && ms.move != null) {
                best = ms.move;
            }
        }

        return best;
    }

   
    private MoveScore alphaBetaRoot(ArrayList<Pieces> board, int depth, int alpha, int beta) {
        ArrayList<int[]> moves = Rules.getLegalMoves(board, aiColor);
        if (moves.isEmpty()) return new MoveScore(null, Heuristic.evaluate(board, aiColor));

        sortMoves(board, moves);

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
            if (alpha >= beta) break; // cutoff
        }

        return new MoveScore(bestMove, bestScore);
    }

  
    private int alphaBeta(ArrayList<Pieces> board, int depth, int alpha, int beta, int turnColor, boolean maximizing) {

        long hash = computeZobrist(board);

   
        TTEntry entry = tt.get(hash);
        if (entry != null && entry.depth >= depth) {
            if (entry.flag == TTEntry.EXACT) return entry.value;
            if (entry.flag == TTEntry.LOWER) alpha = Math.max(alpha, entry.value);
            if (entry.flag == TTEntry.UPPER) beta = Math.min(beta, entry.value);
            if (alpha >= beta) return entry.value;
        }

       
        if (depth <= 0) {
            int q = quiescence(board, alpha, beta, turnColor);
            tt.put(hash, new TTEntry(q, 0, TTEntry.EXACT));
            return q;
        }

        ArrayList<int[]> moves = Rules.getLegalMoves(board, turnColor);
        if (moves.isEmpty()) {
            int val = Heuristic.evaluate(board, aiColor);
            tt.put(hash, new TTEntry(val, depth, TTEntry.EXACT));
            return val;
        }

        sortMoves(board, moves);

        int originalAlpha = alpha;
        int bestValue = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int[] mv : moves) {
            MoveState st = applyMove(board, mv);
            int val = alphaBeta(board, depth - 1, alpha, beta, 1 - turnColor, !maximizing);
            undoMove(board, st);

            if (maximizing) {
                bestValue = Math.max(bestValue, val);
                alpha = Math.max(alpha, val);
            } else {
                bestValue = Math.min(bestValue, val);
                beta = Math.min(beta, val);
            }

            if (alpha >= beta) {
                // tăng trọng số cho nước gây cutoff (history heuristic)
                int from = mv[0] + mv[1] * 8;
                int to = mv[2] + mv[3] * 8;
                history[from][to] += 1 << depth;
                break;
            }
        }

  
        int flag = TTEntry.EXACT;
        if (bestValue <= originalAlpha) flag = TTEntry.UPPER;
        else if (bestValue >= beta) flag = TTEntry.LOWER;

        tt.put(hash, new TTEntry(bestValue, depth, flag));

        return bestValue;
    }

    /**
     * Quiescence search – chỉ xét capture để tránh Horizon Effect.
     */
    private int quiescence(ArrayList<Pieces> board, int alpha, int beta, int turnColor) {
        int standPat = Heuristic.evaluate(board, aiColor);

        if (standPat >= beta) return beta;
        alpha = Math.max(alpha, standPat);

        ArrayList<int[]> captures = generateCaptures(board, turnColor);

        captures.sort((a, b) -> mvvLvaScore(board, b) - mvvLvaScore(board, a));

        for (int[] mv : captures) {
            MoveState st = applyMove(board, mv);
            int score = -quiescence(board, -beta, -alpha, 1 - turnColor);
            undoMove(board, st);

            if (score >= beta) return beta;
            alpha = Math.max(alpha, score);
        }

        return alpha;
    }

   
    private ArrayList<int[]> generateCaptures(ArrayList<Pieces> board, int color) {
        ArrayList<int[]> moves = Rules.getLegalMoves(board, color);
        ArrayList<int[]> caps = new ArrayList<>();
        for (int[] m : moves)
            if (getPiece(board, m[2], m[3]) != null)
                caps.add(m);
        return caps;
    }

   
    private int mvvLvaScore(ArrayList<Pieces> board, int[] mv) {
        Pieces victim = getPiece(board, mv[2], mv[3]);
        Pieces attacker = getPiece(board, mv[0], mv[1]);

        int vVal = (victim == null) ? 0 : Heuristic.getPieceValue(victim);
        int aVal = (attacker == null) ? 0 : Heuristic.getPieceValue(attacker);

        return vVal * 1000 - aVal;
    }

  
    private void sortMoves(ArrayList<Pieces> board, ArrayList<int[]> moves) {
        moves.sort((a, b) -> {
            boolean capA = getPiece(board, a[2], a[3]) != null;
            boolean capB = getPiece(board, b[2], b[3]) != null;

            if (capA && capB)
                return mvvLvaScore(board, b) - mvvLvaScore(board, a);
            if (capA) return -1;
            if (capB) return 1;

            int fromA = a[0] + a[1] * 8;
            int toA = a[2] + a[3] * 8;

            int fromB = b[0] + b[1] * 8;
            int toB = b[2] + b[3] * 8;

            return history[fromB][toB] - history[fromA][toA];
        });
    }

   
    private static class MoveState {
        Pieces piece;
        int fromCol, fromRow;
        int toCol, toRow;
        boolean oldHasMoved;
        Pieces captured;
    }

    private MoveState applyMove(ArrayList<Pieces> board, int[] mv) {
        int fc = mv[0], fr = mv[1], tc = mv[2], tr = mv[3];

        MoveState st = new MoveState();
        st.fromCol = fc;
        st.fromRow = fr;
        st.toCol = tc;
        st.toRow = tr;

        Pieces moving = getPiece(board, fc, fr);
        st.piece = moving;
        st.oldHasMoved = moving.hasMoved;

        Pieces cap = getPiece(board, tc, tr);
        st.captured = cap;
        if (cap != null) board.remove(cap);

        moving.col = tc;
        moving.row = tr;
        moving.hasMoved = true;

        return st;
    }

    private void undoMove(ArrayList<Pieces> board, MoveState st) {
        st.piece.col = st.fromCol;
        st.piece.row = st.fromRow;
        st.piece.hasMoved = st.oldHasMoved;

        if (st.captured != null) board.add(st.captured);
    }

    private static Pieces getPiece(ArrayList<Pieces> board, int c, int r) {
        for (Pieces p : board)
            if (p.col == c && p.row == r)
                return p;
        return null;
    }

    
    private static class TTEntry {
        static final int EXACT = 0, LOWER = 1, UPPER = 2;
        int value, depth, flag;

        TTEntry(int v, int d, int f) {
            value = v; depth = d; flag = f;
        }
    }

   
    private long computeZobrist(ArrayList<Pieces> board) {
        long h = 0L;

        for (Pieces p : board) {
            int idx = pieceToIndex(p);
            int sq = p.row * 8 + p.col;
            if (idx >= 0) h ^= zobristPieceSquare[idx][sq];
        }

        return h;
    }

   
    private int pieceToIndex(Pieces p) {
        int base = (p.color == 1) ? 0 : 6;
        if (p instanceof Pawn)   return base + 0;
        if (p instanceof Knight) return base + 1;
        if (p instanceof Bishop) return base + 2;
        if (p instanceof Rook)   return base + 3;
        if (p instanceof Queen)  return base + 4;
        if (p instanceof King)   return base + 5;
        return -1;
    }

    private static class MoveScore {
        int[] move;
        int score;
        MoveScore(int[] m, int s) { move = m; score = s; }
    }

}

