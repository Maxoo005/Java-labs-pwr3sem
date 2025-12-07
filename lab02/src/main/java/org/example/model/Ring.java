package org.example.model;

public record Ring(int id, int outer, int inner, int height) {
    public boolean isDisk() { return inner == 0; } //true - pełny krążek

    //sprawdzenie czy promień r mieści się w zakresie pierścienia (inner < r ≤ outer)
    public boolean passes(int r) {
        return inner < r && outer >= r;
    }
}
