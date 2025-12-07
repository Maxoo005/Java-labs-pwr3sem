package org.example.service;

import org.example.persistence.db.DataSourceFactory;
import org.example.persistence.db.SqliteBusyMapper;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

//przekąbinowany zegar zdecydowanie zepsułem
public class TimeService {
    private final DataSourceFactory dsf;
    private final int tickMinutes;

    public TimeService(DataSourceFactory dsf, int tickMinutes) {
        this.dsf = dsf;
        this.tickMinutes = tickMinutes;
    }

    /** Zwraca „wirtualne teraz” (UTC) przechowywane w tabeli time_state. */
    public Instant now() {
        final String sqlSel = "SELECT current_utc FROM time_state WHERE id=1";
        final String sqlIns = "INSERT OR IGNORE INTO time_state(id, current_utc) VALUES(1, datetime('now'))";
        try (var c = dsf.getConnection()) {
            try (var st = c.createStatement()) {
                st.executeUpdate(sqlIns);
            }
            try (var st = c.createStatement();
                 var rs = st.executeQuery(sqlSel)) {
                if (rs.next()) {
                    return Instant.parse(rs.getString("current_utc"));
                }
                throw new RuntimeException("Brak wiersza time_state");
            }
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    /** Przesuwa zegar  */
    public Instant tickAndApplyRules() {
        var newNow = now().plus(Duration.ofMinutes(tickMinutes));
        try (var c = dsf.getConnection()) {
            c.setAutoCommit(false);

            try (var ps = c.prepareStatement("UPDATE time_state SET current_utc=? WHERE id=1")) {
                ps.setString(1, newNow.toString());
                ps.executeUpdate();
            }

            var ruleSql = """
                UPDATE reservation
                   SET status = 'NO_SHOW', updated_at_utc = ?
                 WHERE status = 'NEW'
                   AND datetime(date_iso || 'T' || time_iso) <= datetime(?, 'utc')
            """;
            try (var ps = c.prepareStatement(ruleSql)) {
                ps.setString(1, newNow.toString());
                ps.setString(2, newNow.toString());
                ps.executeUpdate();
            }

            c.commit();
            return newNow;
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }
}
