package org.example.model;

import java.util.List;

public record Board(List<Hole> holes, List<Ring> rings) {
    public int holeCount() {
        return holes == null ? 0 : holes.size(); //zwrot liczby otworów
    }
    public int ringCount() {
        return rings == null ? 0 : rings.size();  //zwrot liczby pierścieni
    }
}
