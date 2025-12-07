package org.example.solve;

import org.example.model.Ring;
import java.util.List;

public interface StackingPolicy {
    /** czy można położyć pierścień next na wierzchu stosu current?
     * Zasada Matrioszki
     * Gdy stos pusty to zwraca true
     */
    boolean canPlace(List<Ring> current, Ring next);
}
