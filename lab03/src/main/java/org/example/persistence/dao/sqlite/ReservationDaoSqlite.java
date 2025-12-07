package org.example.persistence.dao.sqlite;

import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationStatus;
import org.example.persistence.dao.ReservationDao;
import org.example.persistence.db.DataSourceFactory;
import org.example.persistence.db.SqliteBusyMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDaoSqlite implements ReservationDao {

    private final DataSourceFactory dsf;

    public ReservationDaoSqlite(DataSourceFactory dsf) {
        this.dsf = dsf;
    }

    @Override
    public Reservation create(Reservation r) {
        final String sql = """
            INSERT INTO reservation(shop_id, service_name, client_id, employee_id,
                                    date_iso, time_iso, status,
                                    created_at_utc, updated_at_utc, price_cents)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (var c = dsf.getConnection();
             var ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getShopId());
            ps.setString(2, r.getServiceName());
            if (r.getClientId() == null) ps.setNull(3, java.sql.Types.BIGINT); else ps.setLong(3, r.getClientId());
            if (r.getEmployeeId() == null) ps.setNull(4, java.sql.Types.BIGINT); else ps.setLong(4, r.getEmployeeId());
            ps.setString(5, r.getDate().toString());     // YYYY-MM-DD
            ps.setString(6, r.getTime().toString());     // HH:mm
            ps.setString(7, r.getStatus().name());
            ps.setString(8, r.getCreatedAtUtc().toString());
            ps.setString(9, r.getUpdatedAtUtc().toString());
            ps.setInt(10, r.getPriceCents());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }
            return r;
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public Optional<Reservation> findById(long id) {
        final String sql = """
            SELECT id, shop_id, service_name, client_id, employee_id,
                   date_iso, time_iso, status, created_at_utc, updated_at_utc, price_cents
            FROM reservation WHERE id=?
            """;
        try (var c = dsf.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findById failed id=" + id, e);
        }
    }

    @Override
    public List<Reservation> findByClient(long clientId) {
        final String sql = """
            SELECT id, shop_id, service_name, client_id, employee_id,
                   date_iso, time_iso, status, created_at_utc, updated_at_utc, price_cents
            FROM reservation
            WHERE client_id=?
            ORDER BY date_iso, time_iso, id
            """;
        try (var c = dsf.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, clientId);
            try (var rs = ps.executeQuery()) {
                List<Reservation> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findByClient failed clientId=" + clientId, e);
        }
    }

    @Override
    public List<Reservation> findByShopAndDate(long shopId, LocalDate date) {
        final String sql = """
            SELECT id, shop_id, service_name, client_id, employee_id,
                   date_iso, time_iso, status, created_at_utc, updated_at_utc, price_cents
            FROM reservation
            WHERE shop_id=? AND date_iso=?
            ORDER BY time_iso, id
            """;
        try (var c = dsf.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setString(2, date.toString());
            try (var rs = ps.executeQuery()) {
                List<Reservation> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findByShopAndDate failed", e);
        }
    }

    @Override
    public List<Reservation> findAnonByShopAndDate(long shopId, LocalDate date) {
        final String sql = """
            SELECT id, shop_id, service_name,
                   NULL as client_id, NULL as employee_id,
                   date_iso, time_iso, status, created_at_utc, updated_at_utc, price_cents
            FROM reservation
            WHERE shop_id=? AND date_iso=?
            ORDER BY time_iso, id
            """;
        try (var c = dsf.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setString(2, date.toString());
            try (var rs = ps.executeQuery()) {
                List<Reservation> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findAnonByShopAndDate failed", e);
        }
    }

    @Override
    public boolean update(Reservation r, Instant expectedUpdatedAt) {
        final String sql = """
            UPDATE reservation
            SET shop_id = ?,
                service_name = ?,
                client_id = ?,
                employee_id = ?,
                date_iso = ?,
                time_iso = ?,
                status = ?,
                created_at_utc = ?,
                updated_at_utc = ?,
                price_cents = ?
            WHERE id = ?
              AND updated_at_utc = ?
            """;
        try (var c = dsf.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, r.getShopId());
            ps.setString(2, r.getServiceName());
            if (r.getClientId() == null) ps.setNull(3, java.sql.Types.BIGINT); else ps.setLong(3, r.getClientId());
            if (r.getEmployeeId() == null) ps.setNull(4, java.sql.Types.BIGINT); else ps.setLong(4, r.getEmployeeId());
            ps.setString(5, r.getDate().toString());
            ps.setString(6, r.getTime().toString());
            ps.setString(7, r.getStatus().name());
            ps.setString(8, r.getCreatedAtUtc().toString());
            ps.setString(9, r.getUpdatedAtUtc().toString());     // nowa wartość
            ps.setInt(10, r.getPriceCents());
            ps.setLong(11, r.getId());
            ps.setString(12, expectedUpdatedAt.toString());       // stara wartość (warunek)
            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public boolean delete(long id, Instant expectedUpdatedAt) {
        final String sql = """
            DELETE FROM reservation
            WHERE id = ? AND updated_at_utc = ?
            """;
        try (var c = dsf.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, expectedUpdatedAt.toString());
            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public List<Reservation> listForEmployeeOnDate(long employeeId, LocalDate date) {
        final String sql = """
        SELECT id, shop_id, service_name, client_id, employee_id,
               date_iso, time_iso, status, created_at_utc, updated_at_utc, price_cents
        FROM reservation
        WHERE employee_id = ? AND date_iso = ?
        ORDER BY time_iso ASC
        """;
        try (var conn = dsf.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, employeeId);
            ps.setString(2, date.toString());
            try (var rs = ps.executeQuery()) {
                var out = new ArrayList<Reservation>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error listForEmployeeOnDate: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reservation> listForShopOnDate(long shopId, LocalDate date) {
        final String sql = """
        SELECT id, shop_id, service_name, client_id, employee_id,
               date_iso, time_iso, status, created_at_utc, updated_at_utc, price_cents
        FROM reservation
        WHERE shop_id = ? AND date_iso = ?
        ORDER BY time_iso ASC
        """;
        try (var conn = dsf.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, shopId);
            ps.setString(2, date.toString());
            try (var rs = ps.executeQuery()) {
                var out = new ArrayList<Reservation>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error listForShopOnDate: " + e.getMessage(), e);
        }
    }

    // --- mapowanie pomocnicze ---
    private static Reservation map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        long shopId = rs.getLong("shop_id");
        String serviceName = rs.getString("service_name");
        long cId = rs.getLong("client_id");
        Long clientId = rs.wasNull() ? null : cId;
        long eId = rs.getLong("employee_id");
        Long employeeId = rs.wasNull() ? null : eId;

        var date = LocalDate.parse(rs.getString("date_iso"));   // YYYY-MM-DD
        var time = LocalTime.parse(rs.getString("time_iso"));   // HH:mm lub HH:mm:ss
        var status = ReservationStatus.valueOf(rs.getString("status"));
        var created = Instant.parse(rs.getString("created_at_utc"));
        var updated = Instant.parse(rs.getString("updated_at_utc"));
        int price = rs.getInt("price_cents");

        return new Reservation(id, shopId, serviceName, clientId, employeeId,
                date, time, status, created, updated, price);
    }
}
