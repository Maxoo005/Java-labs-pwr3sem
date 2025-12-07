package org.example.model;

import org.example.board.Board;

public class Seeker extends Figure {
    private int treasuresCollected = 0;

    public Seeker(Board board, long moveDelayMs) {
        super(board, FigureType.SEEKER, moveDelayMs);
    }

    public void collectTreasure() {
        treasuresCollected++;

        board.notifyTreasureCount(treasuresCollected);
        //gdy zbierze 10 skarbów to zmienia się w strzelca
        if (treasuresCollected >= 10) {

            int x = this.getX();
            int y = this.getY();
            Direction dir = this.getDirection();

            //nowy Shooter
            Shooter shooter = new Shooter(board, 300, 1000);
            shooter.setDirection(dir);

            // pomienienie figury
            board.replaceFigure(x, y, shooter);

            // start nowego strzelca
            Thread t = new Thread(shooter);
            t.setDaemon(true);
            t.start();

            // Seeker znika
            this.setAlive(false);
        }
    }

    @Override
    protected void doAction() {}
}
