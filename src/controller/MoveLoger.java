package controller;

import java.util.ArrayList;
import java.util.List;

// Class nay de luu lai cac nuoc da di


public class MoveLoger {
    private final List<String> moves;

    public MoveLoger() {
        moves = new ArrayList<>();
    }

    public void addMove(String move) {
        moves.add(move);
    }

    // MỞ RỘNG THÊM: " AI ĐANG SUY NGHĨ"
    public void removeLast() {
        if (!moves.isEmpty())
            moves.remove(moves.size() - 1);
    }

    public List<String> getMoves() {
        return moves;
    }

    public void clear() {
        moves.clear();
    }
}

