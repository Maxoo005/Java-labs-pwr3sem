package org.example.persistence.db;

import org.example.apps.config.AppConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceFactory {

    private final String jdbcUrl;

    public DataSourceFactory(AppConfig cfg) {
        var dbPath = Path.of(cfg.get("db.path"));
        this.jdbcUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();
    }

    public Connection getConnection() throws SQLException {
        var conn = DriverManager.getConnection(jdbcUrl);
        // lekkie ustawienia SQLite
        try (var st = conn.createStatement()) {
            st.execute("PRAGMA journal_mode=WAL;");
            st.execute("PRAGMA foreign_keys=ON;");
            st.execute("PRAGMA busy_timeout=3000;");
        }
        return conn;
    }

    /** Tworzy katalog na bazę jeśli trzeba  */
    public static void ensureParentExists(Path dbFile) {
        try {
            var dir = dbFile.toAbsolutePath().getParent();
            if (dir != null && !Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (Exception e) {
            throw new RuntimeException("Nie mogę utworzyć katalogu dla DB: " + dbFile, e);
        }
    }
}
