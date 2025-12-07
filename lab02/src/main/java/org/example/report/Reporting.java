package org.example.report;

import org.example.model.Hole;
import org.example.model.Ring;
import org.example.model.RingStack;

import java.util.List;
import java.util.Locale;

public final class Reporting {

    private Reporting() {}

    //Bubble sort malejąco po promieniu otworu
    public static void bubbleSortHolesDesc(List<Hole> holes) {
        if (holes == null || holes.size() < 2) return;
        boolean swapped;
        for (int i = 0; i < holes.size(); i++) {
            swapped = false;
            for (int j = 0; j < holes.size() - 1 - i; j++) {
                if (holes.get(j).radius() < holes.get(j + 1).radius()) {
                    Hole tmp = holes.get(j);
                    holes.set(j, holes.get(j + 1));
                    holes.set(j + 1, tmp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

     //dla każdego otworu liczba pasujących pierścieni
    public static void printEligibility(List<Hole> holes, List<Ring> rings, int overhangMax) {
        if (holes == null || rings == null) return;
        final boolean limit = overhangMax > 0;

        System.out.println("\n--- RAPORT (prosty) DOPASOWANIA ---");
        for (int i = 0; i < holes.size(); i++) {
            Hole h = holes.get(i);
            int R = h.radius();

            int fitCount = 0;
            int sumOuter = 0;

            for (int k = 0; k < rings.size(); k++) {
                Ring r = rings.get(k);
                boolean innerOk = r.inner() < R;
                boolean outerOk = r.outer() >= R;
                boolean overhangOk = !limit || r.outer() <= R + overhangMax;

                if (innerOk && outerOk && overhangOk) {
                    fitCount++;
                    sumOuter += r.outer();
                }
            }

            System.out.printf(Locale.ROOT,
                    "Otwór #%d (R=%d): pasuje %d szt., suma outer=%d%n",
                    h.id(), R, fitCount, sumOuter);
        }
        System.out.println("=== [KONIEC RAPORTU] ===\n");
    }

    /**
     * Proste podsumowanie rozwiązania:
     * - na otwór: liczba użytych, suma wysokości, czy zamknięty
     * -łączna liczba pierścieni i łączna wysokość wszystkich
     */
    public static void printSolutionSummary(List<RingStack> stacks) {
        if (stacks == null) return;

        System.out.println("\n=== [PODSUMOWANIE] ===");
        int totalRings = 0;
        int totalHeight = 0;
        boolean allClosed = true;

        for (int i = 0; i < stacks.size(); i++) {
            RingStack s = stacks.get(i);
            int usedCount = s.rings().size();
            int usedHeight = 0;
            for (int j = 0; j < s.rings().size(); j++) {
                usedHeight += s.rings().get(j).height();
            }

            totalRings += usedCount;
            totalHeight += usedHeight;

            boolean closed = s.isClosed();
            if (!closed) allClosed = false;

            System.out.printf(Locale.ROOT,
                    "Otwór #%d (R=%d): użyto %d szt., suma H=%d, zamknięty=%s%n",
                    s.hole().id(), s.hole().radius(), usedCount, usedHeight, closed ? "TAK" : "NIE");
        }

        System.out.printf(Locale.ROOT,
                "SUMA: pierścieni=%d, łączna wysokość=%d, wszystkie zamknięte=%s%n",
                totalRings, totalHeight, allClosed ? "TAK" : "NIE");
        System.out.println("=== [KONIEC] ===\n");
    }
}
