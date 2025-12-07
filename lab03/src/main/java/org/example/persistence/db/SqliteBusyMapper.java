package org.example.persistence.db;

import org.example.domain.exception.FileLockedOrBusyException;
import java.sql.SQLException;

public final class SqliteBusyMapper {
    private SqliteBusyMapper() {}

    public static RuntimeException rethrow(SQLException e) {
        var msg = e.getMessage();
        if (msg != null && (msg.contains("SQLITE_BUSY") || msg.contains("database is locked"))) {
            return new FileLockedOrBusyException("Plik bazy jest zajęty. Spróbuj ponownie za chwilę.", e);
        }
        return new RuntimeException(e);
    }
}