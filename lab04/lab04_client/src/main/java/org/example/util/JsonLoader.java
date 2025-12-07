package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonLoader {

    public static String loadFromResources(String fileName) {
        ClassLoader cl = JsonLoader.class.getClassLoader();

        try (InputStream is = cl.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Nie znaleziono pliku w resources: " + fileName);
            }
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas czytania pliku: " + fileName, e);
        }
    }
}
