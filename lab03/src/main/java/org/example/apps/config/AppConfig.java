package org.example.apps.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Czytanie konfiguracji z pliku .properties
 */

public class AppConfig {

    private final Properties props = new Properties(); //klucz-wartość
    private final Path configPath; //scieżka do pliku konfiguracyjnego

    //konstruktor gdzie jest plik config.
    public AppConfig() {
        this.configPath = resolveConfigPath();
        load();
    }

    //sprawdzenie 3 możliwych ścieżek z dojścia do pliku config.
    private Path resolveConfigPath() {
        return Path.of(
                System.getProperty("app.config",
                        System.getenv().getOrDefault("APP_CONFIG",
                                "config/application.properties"))
        );
    }

    //sprawdza czy plik istnieje
    private void load() {
        if (!Files.exists(configPath)) {
            throw new IllegalStateException("Nie znaleziono pliku konfiguracyjnego: " + configPath.toAbsolutePath());
        }
        try (var in = Files.newInputStream(configPath)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Błąd wczytywania konfiguracji z: " + configPath.toAbsolutePath(), e);
        }
    }

    //Pobiera wartość dla pewnego klucza z konfig.
    public String get(String key) {
        String v = props.getProperty(key);
        if (v == null) {
            throw new IllegalStateException("Brak klucza w konfiguracji: " + key + " (plik: " + configPath.toAbsolutePath() + ")");
        }
        return v;
    }

    //jeśli nie znajdzie klucza zwraca wartość def
    public String getOrDefault(String key, String def) {
        return props.getProperty(key, def);
    }
}
