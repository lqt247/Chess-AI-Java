package ai;

import model.*;
import java.util.ArrayList;
import java.util.Random;

public class SimpleAI implements AI {
    private int color;
    private Random random = new Random();

    public SimpleAI(int color) {
        this.color = color;
    }

    @Override
    public int[] chooseMove(ArrayList<Pieces> pieces) {
        ArrayList<int[]> possibleMoves = new ArrayList<>();

        for (Pieces p : pieces) {
            if (p.color != color) continue;
            for (int c = 0; c < 8; c++) {
                for (int r = 0; r < 8; r++) {
                    if (p.canMove(c, r)) {
                        possibleMoves.add(new int[]{p.col, p.row, c, r, pieces.indexOf(p)});
                    }
                }
            }
        }

        if (possibleMoves.isEmpty()) return null;
        return possibleMoves.get(random.nextInt(possibleMoves.size()));
    }
}
