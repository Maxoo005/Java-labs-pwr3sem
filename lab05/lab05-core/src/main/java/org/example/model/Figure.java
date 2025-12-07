package org.example.model;

import org.example.board.Board;
import java.util.Random;

public abstract class Figure implements Runnable {

    protected final Board board;
    protected volatile boolean alive = true;
    protected int x;
    protected int y;
    protected Direction direction;
    protected final Random random = new Random();
    protected final long moveDelayMs;
    protected final FigureType type;

    public Figure(Board board, FigureType type, long moveDelayMs) {
        this.board = board;
        this.type = type;
        this.moveDelayMs = moveDelayMs;
        this.direction = Direction.values()[random.nextInt(Direction.values().length)];
    }

    // teleport na drugą strone 1/2
    protected int wrapX(int x) {
        if (x < 0) return board.getWidth() - 1;
        if (x >= board.getWidth()) return 0;
        return x;
    }

    //teleport 2/2
    protected int wrapY(int y) {
        if (y < 0) return board.getHeight() - 1;
        if (y >= board.getHeight()) return 0;
        return y;
    }

    public FigureType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //dołożenie nowej figury
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    protected abstract void doAction();

    @Override
    public void run() {
        // wątek działa do momentu gdy figura nie została usunięta/zmieniona
        while (alive && !Thread.currentThread().isInterrupted()) {
            try {
                board.moveOneStep(this);
                doAction();
                Thread.sleep(moveDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
