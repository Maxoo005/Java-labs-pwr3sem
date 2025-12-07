package org.example.model;

import org.example.board.Board;

public class Pusher extends Figure {

    public Pusher(Board board, long moveDelayMs) {
        super(board, FigureType.PUSHER, moveDelayMs);
    }

    @Override
    protected void doAction() {}
}
