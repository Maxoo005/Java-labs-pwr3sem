package org.example.solve;

import org.example.model.Ring;
import java.util.List;

public class MatrioszkaPolicy implements StackingPolicy {

    @Override
    public boolean canPlace(List<Ring> current, Ring next) {
        // pusty stos – można kłaść
        if (current == null || current.isEmpty()) return true;

        // ostatni (górny) element stosu
        int topOuter = current.get(current.size() - 1).outer();

        // zasada matrioszki: kolejny pierścień nie może być "szerszy" od poprzedniego
        // (czyli jego outer nie większy niż outer pierścienia leżącego niżej)
        return next.outer() <= topOuter;
    }
}
