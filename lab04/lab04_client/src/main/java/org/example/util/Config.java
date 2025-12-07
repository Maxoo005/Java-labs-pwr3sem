package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader()
                .getResourceAsStream("lab04_client.properties")) {

            if (is == null) {
                throw new RuntimeException("Brak pliku konfiguracyjnego lab04_client.properties w resources");
            }
            PROPS.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas ładowania konfiguracji", e);
        }
    }

    public static String get(String key) {
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Brak klucza w konfiguracji: " + key);
        }
        return value;
    }
}
