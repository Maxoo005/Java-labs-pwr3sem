package org.example.creator;

import org.example.board.Board;
import org.example.board.Cell;
import org.example.model.Figure;
import org.example.model.Pusher;
import org.example.model.Seeker;
import org.example.model.Shooter;

import java.util.Random;

public class Creator implements Runnable {

    private final Board board;
    private final long delayMs; //opóźnienie
    private final double treasureProbability;  //zamiast figury moze byc skarb,

    private final Random random = new Random();
    private volatile boolean running = true;

    private static final int MAX_FIGURES = 3;   // ostatecznie maksymalnie 3 figury jednocześnie

    /* pojawianie sie na planszy w kolejnosci:
        SEEKER\
        SHOOTER
        PUSHER
        zapętlone generowanie figur
     */
    private int nextType = 0;

    public Creator(Board board,
                   long delayMs,
                   double treasureProbability,
                   double seekerProbability,
                   double shooterProbability,
                   double pusherProbability) {

        this.board = board;
        this.delayMs = delayMs;
        this.treasureProbability = treasureProbability;
    }


    // zlicznanie skarób na planszy
    private int countTreasures() {
        int count = 0;
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell c = board.getCell(x, y);
                if (c != null && c.hasTreasure()) {
                    count++;
                }
            }
        }
        return count;
    }

    //zliczanie figur na na planszy
    private int countFigures() {
        int count = 0;
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.getCell(x, y).getFigure() != null) {
                    count++;
                }
            }
        }
        return count;
    }

    //pętla generatora
    @Override
    public void run() {
        int w = board.getWidth();
        int h = board.getHeight();

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                int x = random.nextInt(w);
                int y = random.nextInt(h);

                board.setCreatorHere(x, y, true);

                //cały czas minimum 5 skarbów na planszy
                int treasureCount = countTreasures();
                while (treasureCount < 5) {
                    board.placeTreasureRandom();
                    treasureCount++;
                }

                //generowanie figur, baisic ustawiona maxymalnie 3
                if (countFigures() < MAX_FIGURES) {

                    if (random.nextDouble() < treasureProbability) {
                        // dodatkowy skarb, zamiast figury moze pokazać się skarb
                        if (treasureCount < 5) {
                            board.placeTreasureRandom();
                            treasureCount++;
                        }

                    } else {

                        Figure fig = null;

                        switch (nextType) {
                            //seeker
                            case 0 -> fig = new Seeker(board, 1200);
                            //shooter
                            case 1 -> fig = new Shooter(board, 1500, 2500);
                            //pusher
                            case 2 -> fig = new Pusher(board, 1300);
                        }

                        nextType = (nextType + 1) % 3;

                        //rozmieszczanie na planszy
                        if (fig != null && board.placeFigureRandomly(fig)) {
                            Thread t = new Thread(fig);
                            t.setDaemon(true);
                            t.start();
                        }
                    }
                }

                //flaga out
                board.setCreatorHere(x, y, false);

                Thread.sleep(delayMs);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
