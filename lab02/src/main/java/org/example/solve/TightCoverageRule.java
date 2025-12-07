package org.example.solve;

import org.example.model.Hole;
import org.example.model.Ring;

//klasa odpowiadająca za maksymalne dopuszczone naddanie pierścienia na otwór

public class TightCoverageRule implements CoverageRule {
    private final int overhangMax;

    public TightCoverageRule(int overhangMax) {
        this.overhangMax = overhangMax;
    }

    @Override
    public boolean contributes(Hole hole, Ring ring) {
        int R = hole.radius();
        int inner = ring.inner();
        int outer = ring.outer();
        return inner < R && R <= outer && outer <= R + overhangMax;
    }
}
