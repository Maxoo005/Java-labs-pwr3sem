package org.example.apps.util;

import java.time.LocalDate;

//klasy final nie można dziedziczyć
public final class Cli {
    public static long argLong(String[] args, String key, boolean required) {
        //pomocnicza metoda wyszukuje wartość pod kluczem
        String v = argValue(args, key);
        if (v == null) {
            if (required) throw new IllegalArgumentException("Brak argumentu " + key);
            return 0L;
        }
        /**jesli nie znalezniono argumentu,
         *   i był wymaga wyjącek z komunikatorem
         *   jeśli nie to błąd
         */
      //próba konwersji na liczbe long
        try {
            return Long.parseLong(v);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Argument " + key + " musi być long, otrzymano: " + v);
        }
    }
    //DATA Y M D
    public static LocalDate argDate(String[] args, String key, boolean required, LocalDate def) {
        String v = argValue(args, key);
        if (v == null) {
            if (required) throw new IllegalArgumentException("Brak argumentu " + key);
            return def;
        }
        return LocalDate.parse(v);
    }
        //szuka w ttablicy args
    private static String argValue(String[] args, String key) {
        for (int i = 0; i < args.length - 1; i++) {
            if (key.equals(args[i])) return args[i + 1];
        }
        return null;
    }
}
