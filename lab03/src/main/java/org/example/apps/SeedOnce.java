package org.example.apps;

import org.example.apps.config.AppConfig;
import org.example.persistence.db.DataSourceFactory;
import org.example.persistence.db.Migration;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public class SeedOnce {

    public static void main(String[] args) throws Exception {
        System.out.println("=== SeedOnce (ISO timestamps) ===");

        var cfg = new AppConfig();
        var dsf = new DataSourceFactory(cfg);
        Migration.ensure(cfg, dsf);

        try (var conn = dsf.getConnection()) {
            conn.setAutoCommit(false);
            try (var st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }

            // Sklepy
            insertShop(conn, 1, "Domyślny Salon", null, 3);
            insertShop(conn, 2, "Salon Centrum", null, 4);

            // Ludzie (u nas wszyscy w 'client' z rolą)
            insertClient(conn, 1,   1, "Jan Klient",    "CLIENT");
            insertClient(conn, 2,   2, "Kasia Klientka","CLIENT");
            insertClient(conn, 100, 1, "Adam Fryzjer",  "EMPLOYEE");
            insertClient(conn, 101, 1, "Ewa Stylistka", "EMPLOYEE");
            insertClient(conn, 200, 1, "Oskar Właściciel", "OWNER");
            try (var ps = conn.prepareStatement("UPDATE shop SET owner_client_id=? WHERE id=?")) {
                ps.setLong(1, 200); ps.setLong(2, 1); ps.executeUpdate();
            }

            // Cennik
            insertService(conn, 1, "golenie", 8000);
            insertService(conn, 2, "Strzyżenie męskie", 7500);
            insertService(conn, 3, "Strzyżenie nożyczkami", 9000);
            insertService(conn, 4, "Combo: włosy + broda", 14000);

            var today = LocalDate.now();
            var tomorrow = today.plusDays(1);
            var yesterday = today.minusDays(1);

            // Rezerwacje – ISO „T…Z” w created/updated
            makeReservation(conn, 1, 1, "golenie",               1, null,    tomorrow, LocalTime.of(10, 0), "NEW",        8000);
            makeReservation(conn, 2, 1, "Strzyżenie męskie",     1, null,    tomorrow, LocalTime.of(11,30), "NEW",        7500);
            makeReservation(conn, 3, 1, "golenie",               1, 100L,    tomorrow, LocalTime.of(12, 0), "CONFIRMED",  8000);

            makeReservation(conn, 4, 1, "Combo: włosy + broda",  2, 100L,    today,    LocalTime.of(23, 59), "CONFIRMED", 14000);
            makeReservation(conn, 5, 1, "Strzyżenie nożyczkami", 2, 100L,    yesterday,LocalTime.of(16,30), "DONE",       9000);
            makeReservation(conn, 6, 2, "golenie",               1, 101L,    yesterday,LocalTime.of(17, 0), "PAID",       8000);
            makeReservation(conn, 7, 1, "golenie",               1, 100L,    yesterday,LocalTime.of(18, 0), "NO_SHOW",    8000);
            makeReservation(conn, 8, 1, "Strzyżenie męskie",     1, null,    tomorrow, LocalTime.of(19, 0), "CANCELED",   7500);
            makeReservation(conn, 9, 2, "Strzyżenie nożyczkami", 2, 101L,    tomorrow.plusDays(1), LocalTime.of(9, 0),  "CONFIRMED", 9000);
            makeReservation(conn,10, 2, "Combo: włosy + broda",  1, 100L,    tomorrow.plusDays(2), LocalTime.of(14, 0), "NEW",      14000);

            conn.commit();

            show(conn, "SELECT id, name, chairs, owner_client_id FROM shop ORDER BY id");
            show(conn, "SELECT id, shop_id, name, role FROM client ORDER BY id");
            show(conn, "SELECT id, name, price_cents FROM service_price ORDER BY id");
            show(conn, "SELECT id, shop_id, service_name, client_id, employee_id, date_iso, time_iso, status, created_at_utc, updated_at_utc FROM reservation ORDER BY id");

            System.out.println("=== SeedOnce: OK ===");
        }
    }

    // ===== helpers =====

    private static String nowIsoZ() {
        return Instant.now().toString(); // np. 2025-11-09T23:34:42Z
    }

    private static void cleanup(Connection conn) throws SQLException {
        exec(conn, "DELETE FROM reservation");
        exec(conn, "DELETE FROM service_price");
        exec(conn, "UPDATE shop SET owner_client_id=NULL");
        exec(conn, "DELETE FROM client");
        exec(conn, "DELETE FROM shop");
        // time_state zostawiamy
    }

    private static void insertShop(Connection conn, long id, String name, Long ownerClientId, int chairs) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO shop(id, name, chairs, owner_client_id) VALUES (?, ?, ?, ?)")) {
            ps.setLong(1, id);
            ps.setString(2, name);
            ps.setInt(3, chairs);
            if (ownerClientId == null) ps.setNull(4, Types.BIGINT); else ps.setLong(4, ownerClientId);
            ps.executeUpdate();
        }
    }

    private static void insertClient(Connection conn, long id, long shopId, String name, String role) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO client(id, shop_id, name, role) VALUES (?, ?, ?, ?)")) {
            ps.setLong(1, id);
            ps.setLong(2, shopId);
            ps.setString(3, name);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    private static void insertService(Connection conn, long id, String name, int priceCents) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO service_price(id, name, price_cents) VALUES (?, ?, ?)")) {
            ps.setLong(1, id);
            ps.setString(2, name);
            ps.setInt(3, priceCents);
            ps.executeUpdate();
        }
    }

    private static void makeReservation(
            Connection conn, long id, long shopId, String serviceName, long clientId, Long employeeId,
            LocalDate date, LocalTime time, String status, int priceCents
    ) throws SQLException {
        try (var ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO reservation(" +
                        "id, shop_id, service_name, client_id, employee_id, date_iso, time_iso, status, " +
                        "created_at_utc, updated_at_utc, price_cents)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            ps.setLong(1, id);
            ps.setLong(2, shopId);
            ps.setString(3, serviceName);
            ps.setLong(4, clientId);
            if (employeeId == null) ps.setNull(5, Types.BIGINT); else ps.setLong(5, employeeId);
            ps.setString(6, date.toString());
            ps.setString(7, time.toString());
            ps.setString(8, status);
            ps.setString(9, nowIsoZ());  // ISO-8601
            ps.setString(10, nowIsoZ()); // ISO-8601
            ps.setInt(11, priceCents);
            ps.executeUpdate();
        }
    }

    private static void show(Connection conn, String sql) throws SQLException {
        System.out.println("\n-- " + sql);
        try (var st = conn.createStatement(); var rs = st.executeQuery(sql)) {
            var md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) row.append(" | ");
                    row.append(md.getColumnLabel(i)).append("=").append(rs.getString(i));
                }
                System.out.println(row);
            }
        }
    }

    private static void exec(Connection conn, String sql) throws SQLException {
        try (var st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }
}
