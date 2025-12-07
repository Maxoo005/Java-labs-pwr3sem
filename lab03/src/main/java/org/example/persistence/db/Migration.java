package org.example.persistence.db;

import org.example.apps.config.AppConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;

public class Migration {
    public static void ensure(AppConfig cfg, DataSourceFactory dsf) {
        // utwórz katalog ./data jeśli trzeba
        Path dbFile = Path.of(cfg.get("db.path"));
        DataSourceFactory.ensureParentExists(dbFile);

        // czytaj schema.sql z resources
        String sql = readResource("/schema.sql");
        if (sql == null || sql.isBlank()) {
            throw new IllegalStateException("Brak lub pusty schema.sql w resources!");
        }

        try (Connection c = dsf.getConnection();
             var st = c.createStatement()) {
            // rozbijamy po średniku, wykonujemy pojedyncze CREATE'y
            for (String part : sql.split(";")) {
                var s = part.trim();
                if (!s.isBlank()) {
                    st.execute(s);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Błąd migracji schematu DB", e);
        }
    }

    private static String readResource(String path) {
        try (var in = Migration.class.getResourceAsStream(path)) {
            if (in == null) return null;
            var r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Nie mogę wczytać zasobu: " + path, e);
        }
    }
    private static boolean isTableEmpty(java.sql.Connection c, String table) throws Exception {
        try (var st = c.createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM " + table)) {
            return rs.next() && rs.getInt("cnt") == 0;
        }
    }
    public static void seedIfEmpty(org.example.persistence.db.DataSourceFactory dsf) {
        try (var conn = dsf.getConnection(); var st = conn.createStatement()) {
            conn.setAutoCommit(false);

            if (isTableEmpty(conn, "shop")) {
                System.out.println("[seed] Inicjalizuję dane startowe...");

                st.executeUpdate("""
                INSERT INTO shop(id, name, owner_id) VALUES (1, 'BarberShop Wrocław', 1);

                INSERT INTO person(id, shop_id, name, role) VALUES
                    (1, 1, 'Marek Właściciel', 'OWNER'),
                    (2, 1, 'Ania Fryzjerka', 'EMPLOYEE'),
                    (3, 1, 'Bartek Barber', 'EMPLOYEE'),
                    (4, 1, 'Kasia Kasjerka', 'CASHIER'),
                    (5, 1, 'Adam Klient', 'CLIENT'),
                    (6, 1, 'Ewa Klientka', 'CLIENT');

                INSERT INTO service_price(id, shop_id, service_name, price_cents) VALUES
                    (1, 1, 'Strzyżenie męskie', 3000),
                    (2, 1, 'Golenie brzytwą', 2500),
                    (3, 1, 'Combo (włosy + broda)', 4500);
            """);

                conn.commit();
                System.out.println("[seed] OK.");
            } else {
                System.out.println("[seed] Pomięto – baza nie jest pusta.");
            }
        } catch (Exception e) {
            System.err.println("[seed] Błąd seeda: " + e.getMessage());
        }
    }
}
