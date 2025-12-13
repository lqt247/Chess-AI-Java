package ai;

import java.util.ArrayList;
import java.util.List;

import model.Pieces;


public interface AI {
    int[] chooseMove(ArrayList<Pieces> board, List<String> history);
    private boolean isRepeatMove(List<String> history, String move) {
        int n = history.size();
        if (n < 2) return false;
        return move.equals(history.get(n - 2));
    }

}

