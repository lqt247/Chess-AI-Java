package ai;

import model.Pieces;
import java.util.ArrayList;


public interface AI {
    int[] chooseMove(ArrayList<Pieces> pieces);
}
