package org.example.io;

import org.example.model.Hole;
import org.example.model.Ring;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Najprostszy możliwy generator:
 * - losowe HOLE w przedziale [10..500]
 * - losowe RING:
 *      outer w [12..800], inner w [0..outer-1], height w [1..20]
 * - ~20% to krążki (inner == 0), reszta zwykłe pierścienie
 * - zapis do plyta.txt i pierscienie.txt
 */
public final class DataGenerator {

    private DataGenerator() {}

    // Zakresy – prosto i czytelnie
    private static final int HOLE_MIN   = 10;
    private static final int HOLE_MAX   = 500;

    private static final int OUTER_MIN  = 10;
    private static final int OUTER_MAX  = 500;

    private static final int HEIGHT_MIN = 1;
    private static final int HEIGHT_MAX = 20;

    private static final double DISK_SHARE = 0.20; // 20% krążków

    // Wygodne API bez seeda
    public static void generate(String outDir, int holes, int rings) throws Exception {
        generate(outDir, holes, rings, null);
    }

    // Wersja z opcjonalnym seedem (null -> losowy)
    public static void generate(String outDir, int holes, int rings, Long seed) throws Exception {
        if (holes <= 0 || rings <= 0) {
            throw new IllegalArgumentException("Liczby holes i rings muszą być dodatnie.");
        }

        File dir = new File(outDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Nie mogę utworzyć katalogu: " + dir);
        }

        Random rnd = (seed == null) ? new Random() : new Random(seed);

        // --- HOLES ---
        List<Hole> holeList = new ArrayList<>(holes);
        for (int i = 1; i <= holes; i++) {
            int R = HOLE_MIN + rnd.nextInt(HOLE_MAX - HOLE_MIN + 1);
            holeList.add(new Hole(i, R));
        }

        // --- RINGS ---
        int diskCount = Math.max(1, (int) Math.round(rings * DISK_SHARE));
        if (diskCount >= rings) diskCount = rings - 1; // zawsze zostaw chociaż 1 nie-krążek

        List<Ring> ringList = new ArrayList<>(rings);

        // 1) zwykłe pierścienie (nie-krążki)
        int nonDisks = rings - diskCount;
        for (int i = 0; i < nonDisks; i++) {
            int outer = OUTER_MIN + rnd.nextInt(OUTER_MAX - OUTER_MIN + 1);
            int inner = 1 + rnd.nextInt(outer - 1);                 // [1 .. outer-1] => nie-krążek
            int height = HEIGHT_MIN + rnd.nextInt(HEIGHT_MAX - HEIGHT_MIN + 1);
            ringList.add(new Ring(ringList.size() + 1, outer, inner, height));
        }

        // 2) krążki (inner == 0)
        for (int i = 0; i < diskCount; i++) {
            // żeby krążki były częściej „używalne”, daj outer nieco powyżej losowego otworu
            int R = holeList.get(rnd.nextInt(holeList.size())).radius();
            int minOuter = Math.max(OUTER_MIN, R + 1);
            int maxOuter = Math.min(OUTER_MAX, R + 60);
            if (minOuter > maxOuter) {
                minOuter = OUTER_MIN;
                maxOuter = OUTER_MAX;
            }
            int outer = minOuter + rnd.nextInt(maxOuter - minOuter + 1);
            int height = HEIGHT_MIN + rnd.nextInt(HEIGHT_MAX - HEIGHT_MIN + 1);
            ringList.add(new Ring(ringList.size() + 1, outer, 0, height));
        }

        // --- ZAPIS TXT ---
        try (PrintWriter out = new PrintWriter(new File(dir, "plyta.txt"))) {
            out.println("# nr otworu, promien");
            for (Hole h : holeList) {
                out.printf(Locale.ROOT, "%d, %d%n", h.id(), h.radius());
            }
        }
        try (PrintWriter out = new PrintWriter(new File(dir, "pierscienie.txt"))) {
            out.println("# nr pierscienia, outer, inner, height");
            for (Ring r : ringList) {
                out.printf(Locale.ROOT, "%d, %d, %d, %d%n", r.id(), r.outer(), r.inner(), r.height());
            }
        }
    }
}
