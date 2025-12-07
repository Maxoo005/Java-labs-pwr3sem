package org.example.solve;

import org.example.model.Hole;
import org.example.model.Ring;
//czy można użyć konkretnego pierścienia dla danego otworu
public interface CoverageRule {
    boolean contributes(Hole hole, Ring ring);
}
