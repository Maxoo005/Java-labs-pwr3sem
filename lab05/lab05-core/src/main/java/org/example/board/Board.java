package org.example.board;

import org.example.model.Direction;
import org.example.model.Figure;
import org.example.model.FigureType;
import org.example.model.Seeker;

import java.util.Random;

public class Board {
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final Random random = new Random();
    private BoardListener listener;

    //tleporty1/2
    private int wrapX(int x) {
        if (x < 0) return width - 1;
        if (x >= width) return 0;
        return x;
    }
    //teleporty2/2
    private int wrapY(int y) {
        if (y < 0) return height - 1;
        if (y >= height) return 0;
        return y;
    }

    //plansza i inicjacja pól na planszy
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell();
            }
        }
    }

    public void setListener(BoardListener listener) {
        this.listener = listener;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public synchronized boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private void notifyListener(int x, int y) {
        if (listener != null && inBounds(x, y)) {
            listener.cellUpdated(x, y, cells[y][x]);
        }
    }

    // Stawianie figur
    public synchronized boolean placeFigureRandomly(Figure figure) {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Cell c = cells[y][x];
            if (c.isFreeForFigure()) {
                c.figure = figure;
                figure.setPosition(x, y);
                notifyListener(x, y);
                return true;
            }
        }
        return false;
    }

    // Stawianie skarbów
    public synchronized boolean placeTreasureRandom() {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Cell c = cells[y][x];
            if (!c.treasure && c.figure == null && !c.creatorHere) {
                c.treasure = true;
                notifyListener(x, y);
                return true;
            }
        }
        return false;
    }

    //zaznaczenie gdie kreator
    public synchronized void setCreatorHere(int x, int y, boolean here) {
        if (!inBounds(x, y)) return;
        cells[y][x].creatorHere = here;
        notifyListener(x, y);
    }

    //główna metoda poruszania się po planszy
    public synchronized void moveOneStep(Figure figure) {
        if (!figure.isAlive()) return;

        int x = figure.getX();
        int y = figure.getY();
        Direction dir = figure.getDirection();

        //losowo zmieniamy kierunek poruszania figur by nie chodziły po tych samcyg scieżkach
        if (random.nextDouble() < 0.15) {           // 15% szansy na zmianę kierunku
            Direction[] dirs = Direction.values();
            figure.setDirection(dirs[random.nextInt(dirs.length)]);
            dir = figure.getDirection();            // aktualizacja po zmianie
        }

        int nx = wrapX(x + dir.dx);
        int ny = wrapY(y + dir.dy);

        Cell current = cells[y][x];
        Cell target = cells[ny][nx];

        // jeśli jest kreator to się obracamy
        if (target.creatorHere) {
            figure.setDirection(random.nextBoolean()
                    ? dir.rotateLeft45()
                    : dir.rotateRight45());
            return;
        }

        // PUSHER
        if (figure.getType() == FigureType.PUSHER) {

            if (target.figure != null) {

                int tx2 = wrapX(nx + dir.dx);
                int ty2 = wrapY(ny + dir.dy);
                Cell target2 = cells[ty2][tx2];

                if (target2.isFreeForFigure()) {
                    // przesuwamy figurę o 1 dalej
                    notifyPush(tx2, ty2);

                    target2.figure = target.figure;
                    target.figure.setPosition(tx2, ty2);
                    notifyListener(tx2, ty2);
                } else {
                    // gdy nie ma miejsca wyrzucamy figure z planszy
                    notifyPush(nx, ny);

                    target.figure.setAlive(false);
                    target.figure = null;
                    notifyListener(nx, ny);
                }

                // w obu przypadkach Pusher wchodzi na target
                current.figure = null;
                notifyListener(x, y);

                target.figure = figure;
                figure.setPosition(nx, ny);
                notifyListener(nx, ny);

            } else if (target.isFreeForFigure()) {
                // przed nami pusto – idziemy
                current.figure = null;
                notifyListener(x, y);

                target.figure = figure;
                figure.setPosition(nx, ny);
                notifyListener(nx, ny);

            } else {
                // pole zajęte obracamy sie o 45 w prawo
                figure.setDirection(dir.rotateRight45());
            }

            return;
        }

        // SEEKER i SHOOTER normalne ruchy
        if (target.figure == null) {
            current.figure = null;
            notifyListener(x, y);

            target.figure = figure;
            figure.setPosition(nx, ny);

            if (figure.getType() == FigureType.SEEKER && target.treasure) {
                target.treasure = false;
                ((Seeker) figure).collectTreasure();
            }

            notifyListener(nx, ny);
        } else {
            // gdy sie zderzą to się obracają w prawo lub w lewo
            figure.setDirection(random.nextBoolean()
                    ? dir.rotateLeft45()
                    : dir.rotateRight45());
        }
    }

    // Strzelanie
    public void shoot(Figure shooter) {
        if (shooter.getType() != FigureType.SHOOTER) return;
        synchronized (this) {

            int x = shooter.getX();
            int y = shooter.getY();
            Direction dir = shooter.getDirection();

            int cx = x;
            int cy = y;

            for (int i = 0; i < 3; i++) {
                cx += dir.dx;
                cy += dir.dy;

                if (!inBounds(cx, cy)) break;

                // laser na planszy
                notifyLaser(cx, cy);

                Cell c = cells[cy][cx];

                if (c.creatorHere) break;

                if (c.figure != null) {
                    if (c.figure.getType() == FigureType.PUSHER) break;

                    c.figure.setAlive(false);
                    c.figure = null;
                    notifyListener(cx, cy);
                    break;
                }
            }
        }
    }

    // Dostęp pomocniczy bezp dostep do komórki
    public synchronized Cell getCell(int x, int y) {
        if (!inBounds(x, y)) return null;
        return cells[y][x];
    }

    //podmiana figury
    public synchronized void replaceFigure(int x, int y, Figure newFigure) {
        if (!inBounds(x, y)) return;

        Cell cell = cells[y][x];

        if (cell.figure != null) {
            cell.figure.setAlive(false);
        }

        cell.figure = newFigure;
        newFigure.setPosition(x, y);

        notifyListener(x, y);
    }

    //efekty

    //laser
    private void notifyLaser(int x, int y) {
        Cell cell = cells[y][x];
        cell.setLaserEffect(true);
        notifyListener(x, y);

        new Thread(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {}
            cell.setLaserEffect(false);
            notifyListener(x, y);
        }).start();
    }

    //odpychanie
    private void notifyPush(int x, int y) {
        Cell cell = cells[y][x];
        cell.setPushEffect(true);
        notifyListener(x, y);

        new Thread(() -> {
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {}
            cell.setPushEffect(false);
            notifyListener(x, y);
        }).start();
    }

    //licznik skarbów
    public void notifyTreasureCount(int count) {
        if (listener != null) {
            listener.treasureCountChanged(count);
        }
    }

    public synchronized void killAllFigures() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell c = cells[y][x];
                if (c.figure != null) {
                    c.figure.setAlive(false);
                    c.figure = null;
                    notifyListener(x, y);
                }
            }
        }
    }

    public void resetTreasureCount() {
        if (listener != null) {
            listener.treasureCountChanged(0);
        }
    }
}
