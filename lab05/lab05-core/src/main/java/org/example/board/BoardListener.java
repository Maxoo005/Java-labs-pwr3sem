package org.example.board;

public interface BoardListener {

    //update pól planszy
    void cellUpdated(int x, int y, Cell cell);

    //zmiana liczby skarbów
    default void treasureCountChanged(int count) {
    }
}
