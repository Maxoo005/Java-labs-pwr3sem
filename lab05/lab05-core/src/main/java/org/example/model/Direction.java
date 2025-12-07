package org.example.model;

public enum Direction {
    //kierunki
    N(0, -1),
    NE(1, -1),
    E(1, 0),
    SE(1, 1),
    S(0, 1),
    SW(-1, 1),
    W(-1, 0),
    NW(-1, -1);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    //45 w prawo
    public Direction rotateRight45() {
        int idx = (this.ordinal() + 1) % values().length;
        return values()[idx];
    }

    //45 w lewo
    public Direction rotateLeft45() {
        int idx = (this.ordinal() - 1 + values().length) % values().length;
        return values()[idx];
    }
}
