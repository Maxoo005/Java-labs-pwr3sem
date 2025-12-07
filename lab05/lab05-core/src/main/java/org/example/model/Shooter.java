package org.example.model;

import org.example.board.Board;

public class Shooter extends Figure {

    private long lastShotTime = 0;
    private final long shotIntervalMs;

    public Shooter(Board board, long moveDelayMs, long shotIntervalMs) {
        super(board, FigureType.SHOOTER, moveDelayMs);
        this.shotIntervalMs = shotIntervalMs;
    }

    @Override
    protected void doAction() {
        long now = System.currentTimeMillis();
        if (now - lastShotTime >= shotIntervalMs) {
            board.shoot(this);
            lastShotTime = now;
        }
    }
}
