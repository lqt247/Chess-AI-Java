package ai;

import java.util.ArrayList;
import model.Pieces;

public interface AI {
    int[] chooseMove(ArrayList<Pieces> pieces);
}
