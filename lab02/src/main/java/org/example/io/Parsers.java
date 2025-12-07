package org.example.io;

import org.example.model.Hole;
import org.example.model.Ring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//odczyt plików i zamiana na obiekty
public final class Parsers {

    private Parsers() {}

    public static List<Hole> readHoles(File file) throws IOException {
        List<Hole> list = new ArrayList<>();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",");
                if (p.length < 2) continue;
                int id = Integer.parseInt(p[0].trim());
                int r  = Integer.parseInt(p[1].trim());
                list.add(new Hole(id, r));
            }
        }
        return list;
    }

    public static List<Ring> readRings(File file) throws IOException {
        List<Ring> list = new ArrayList<>();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",");
                if (p.length < 4) continue;
                int id = Integer.parseInt(p[0].trim());
                int outer = Integer.parseInt(p[1].trim());
                int inner = Integer.parseInt(p[2].trim());
                int height = Integer.parseInt(p[3].trim());
                if (inner < 0 || outer <= inner || height <= 0) continue; // sanity
                list.add(new Ring(id, outer, inner, height));
            }
        }
        return list;
    }
}
