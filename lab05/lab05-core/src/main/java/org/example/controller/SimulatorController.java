package org.example.controller;

import org.example.board.Board;
import org.example.creator.Creator;

public class SimulatorController {

    private final Board board;
    private Thread creatorThread = null;

    public SimulatorController(Board board) {
        this.board = board;
    }

    //uruchomienie symulacji
    public void start(long creatorDelay,
                      double treasureProb,
                      double seekerProb,
                      double shooterProb,
                      double pusherProb) {

        // sprawdza by ni uruchomić dwa razy, gdy juz działa
        if (creatorThread != null && creatorThread.isAlive()) return;

        Creator creator = new Creator(
                board,
                creatorDelay,
                treasureProb,
                seekerProb,
                shooterProb,
                pusherProb
        );

        creatorThread = new Thread(creator);
        creatorThread.setDaemon(true);
        creatorThread.start();
    }

    public void stop() {
        // zatrzymanie kreatora
        if (creatorThread != null) {
            creatorThread.interrupt();
        }

        // usunięcie figur
        board.killAllFigures();

        // reset licznika skarbów
        board.resetTreasureCount();
    }

    public Board getBoard() {
        return board;
    }
}
