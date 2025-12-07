package org.example.solve;

import org.example.model.Hole;
import org.example.model.Ring;
import org.example.model.RingStack;

import java.util.*;
/**4-kryteria
 * kazdy stos domknięty jak sie da
 * kazdy pierscień wykorzystany tylko raz
 * kazdy stos ma swój stos
 *OVERHANG ograniczone do 20 */
public class GreedySolver {

    private static final int OVERHANG_MAX = 20;

    private final Objective objective;
    private final CoverageRule coverage = new TightCoverageRule(OVERHANG_MAX);
    private final StackingPolicy policy = new MatrioszkaPolicy();

    public GreedySolver(Objective objective) {
        this.objective = Objects.requireNonNull(objective);
    }
    //kopia listy by usuwać wykorzystane, sortowanie, jak stos sie uda dodajemy do listy z wynikam
    public List<RingStack> solve(List<Hole> holes, List<Ring> rings) {
        List<Ring> pool = new ArrayList<>(rings);

        // większe otwory najpierw
        List<Hole> sortedHoles = new ArrayList<>(holes);
        sortedHoles.sort(Comparator.comparingInt(Hole::radius).reversed());

        List<RingStack> result = new ArrayList<>();

        for (Hole h : sortedHoles) {
            RingStack stack = buildStackForHole(h, pool);
            if (stack != null && !stack.rings().isEmpty()) {
                result.add(stack);
            }
        }
        return result;
    }
    //początek największy, później mniejsze, zamknięty krążkiem
    private RingStack buildStackForHole(Hole hole, List<Ring> pool) {
        RingStack stack = new RingStack(hole);

        while (true) {
            List<Ring> candidates = new ArrayList<>();
            for (Ring r : pool) {
                if (coverage.contributes(hole, r) && policy.canPlace(stack.rings(), r)) {
                    candidates.add(r);
                }
            }

            if (candidates.isEmpty()) {
                Ring disk = pickBestDisk(hole, pool, stack);
                if (disk != null) {
                    stack.add(disk);
                    pool.remove(disk);
                }
                break;
            }

            Ring next = chooseByObjective(candidates);
            if (next == null) break;

            stack.add(next);
            pool.remove(next);
            if (next.isDisk()) break; // stos zamknięty
        }

        // jeśli coś leży, a nie jest zamknięte – zamknięcie krążkiem
        if (!stack.rings().isEmpty() && !stack.isClosed()) {
            Ring disk = pickBestDisk(hole, pool, stack);
            if (disk != null) {
                stack.add(disk);
                pool.remove(disk);
            }
        }

        return stack;
    }
    // wybór najlepszego krążka (inner==0), który pasuje
    private Ring pickBestDisk(Hole hole, List<Ring> pool, RingStack stack) {
        Ring best = null;
        for (Ring r : pool) {
            if (!r.isDisk()) continue;
            if (!coverage.contributes(hole, r)) continue;
            if (!policy.canPlace(stack.rings(), r)) continue;

            if (best == null) best = r;
            else {
                // ciaśniejsze domknięcie: outer najbliżej promienia otworu
                int diffBest = best.outer() - hole.radius();
                int diffR = r.outer() - hole.radius();
                if (diffR < diffBest) best = r;
            }
        }
        return best;
    }

    //4 kryteria
    private Ring chooseByObjective(List<Ring> candidates) {
        Comparator<Ring> cmp;
        switch (objective) {
            case MINH_MINC -> {
                // minimalna wysokość, przy remisie mniejszy outer
                cmp = Comparator.comparingInt(Ring::height)
                        .thenComparingInt(Ring::outer);
            }
            case MINH_MAXC -> {
                // minimalna wysokość, przy remisie większy outer
                cmp = Comparator.comparingInt(Ring::height)
                        .thenComparing(Comparator.comparingInt(Ring::outer).reversed());
            }
            case MAXH_MINC -> {
                // maksymalna wysokość, przy remisie mniejszy outer
                cmp = Comparator.comparingInt(Ring::height).reversed()
                        .thenComparingInt(Ring::outer);
            }
            case MAXH_MAXC -> {
                // maksymalna wysokość, przy remisie większy outer
                cmp = Comparator.comparingInt(Ring::height).reversed()
                        .thenComparing(Comparator.comparingInt(Ring::outer).reversed());
            }
            default -> throw new IllegalStateException("Nieobsługiwany objective: " + objective);
        }
        return candidates.stream().sorted(cmp).findFirst().orElse(null);
    }
}
