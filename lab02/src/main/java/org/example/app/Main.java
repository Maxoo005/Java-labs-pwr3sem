package org.example.app;

import org.example.io.DataGenerator;
import org.example.io.Parsers;
import org.example.model.Hole;
import org.example.model.Ring;
import org.example.model.RingStack;
import org.example.report.Reporting;
import org.example.solve.GreedySolver;
import org.example.solve.Objective;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== LAB 02 – Planowanie pierścieni ===\n");

        //generowanie danych
        System.out.print("Podaj liczbę otworów: ");
        int holesCount = sc.nextInt();
        System.out.print("Podaj liczbę pierścieni: ");
        int ringsCount = sc.nextInt();
        sc.nextLine();

        String outDir = "src/main/resources/generated";
        System.out.println("\nTrwa generowanie danych...");
        DataGenerator.generate(outDir, holesCount, ringsCount);
        System.out.println("Dane zapisane w katalogu: " + outDir + "\n");

        // Ścieżki do wygenerowanych plików
        File holesFile = new File(outDir + "/plyta.txt");
        File ringsFile = new File(outDir + "/pierscienie.txt");

        // Wczytaj dane
        List<Hole> holes = Parsers.readHoles(holesFile);
        List<Ring> rings = Parsers.readRings(ringsFile);

        // Sortowanie bąbelkowe (zgodnie z instrukcją)
        Reporting.bubbleSortHolesDesc(holes);

        //pętla menu
        while (true) {
            System.out.println("""
                    ===============================
                    Wybierz tryb optymalizacji:
                    1 - MINH_MINC (minimalna wysokość, minimalna liczba pierścieni)
                    2 - MINH_MAXC (minimalna wysokość, maksymalna liczba pierścieni)
                    3 - MAXH_MINC (maksymalna wysokość, minimalna liczba pierścieni)
                    4 - MAXH_MAXC (maksymalna wysokość, maksymalna liczba pierścieni)
                    0 - Zakończ program
                    ===============================
                    """);
            System.out.print("Twój wybór: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println("Zamykanie programu...");
                break;
            }

            Objective objective = switch (choice) {
                case 1 -> Objective.MINH_MINC;
                case 2 -> Objective.MINH_MAXC;
                case 3 -> Objective.MAXH_MINC;
                case 4 -> Objective.MAXH_MAXC;
                default -> null;
            };

            if (objective == null) {
                System.out.println("Niepoprawny wybór. Spróbuj ponownie.\n");
                continue;
            }

            // Uruchom solver dla wybranego celu
            System.out.println("\n>>> Tryb: " + objective + " <<<\n");

            GreedySolver solver = new GreedySolver(objective);
            List<RingStack> stacks = solver.solve(holes, rings);

            stacks.sort(Comparator.comparingInt(s -> s.hole().id()));

            //Wynik
            for (RingStack s : stacks)
                System.out.println(s.pretty());

            // Podsumowanie końcowe
            Reporting.printSolutionSummary(stacks);

            int totalH = stacks.stream().mapToInt(RingStack::heightSum).sum();
            int totalC = stacks.stream().mapToInt(RingStack::count).sum();

            System.out.println("\n== Podsumowanie liczbowe ==");
            System.out.println("Suma wysokości (H): " + totalH);
            System.out.println("Liczba pierścieni (C): " + totalC);
            System.out.println("Zakryte otwory: " + stacks.size() + " / " + holes.size());
            System.out.println("\nPowrót do menu...\n");
        }

        sc.close();
        System.out.println("\n=== KONIEC DZIAŁANIA PROGRAMU ===");
    }
}
