package org.example.persistence.dao.sqlite;

import org.example.domain.model.ServicePrice;
import org.example.persistence.dao.ServicePriceDao;
import org.example.persistence.db.DataSourceFactory;
import org.example.persistence.db.SqliteBusyMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicePriceDaoSqlite implements ServicePriceDao {

    private final DataSourceFactory dsf;

    public ServicePriceDaoSqlite(DataSourceFactory dsf) {
        this.dsf = dsf;
    }

    @Override
    public ServicePrice create(ServicePrice sp) {
        final String sql = "INSERT INTO service_price(name, price_cents) VALUES(?, ?)";
        try (var c = dsf.getConnection();
             var ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sp.getName());
            ps.setInt(2, sp.getPriceCents());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new ServicePrice(id, sp.getName(), sp.getPriceCents());
                }
            }
            throw new RuntimeException("Brak wygenerowanego ID");
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public Optional<ServicePrice> findById(long id) {
        final String sql = "SELECT id, name, price_cents FROM service_price WHERE id=?";
        try (var c = dsf.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public List<ServicePrice> findAll() {
        final String sql = "SELECT id, name, price_cents FROM service_price ORDER BY id ASC";
        try (var c = dsf.getConnection();
             var ps = c.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            var out = new ArrayList<ServicePrice>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public void update(ServicePrice sp) {
        final String sql = "UPDATE service_price SET name=?, price_cents=? WHERE id=?";
        try (var c = dsf.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, sp.getName());
            ps.setInt(2, sp.getPriceCents());
            ps.setLong(3, sp.getId());
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Nie znaleziono id=" + sp.getId());
            }
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    @Override
    public void delete(long id) {
        final String sql = "DELETE FROM service_price WHERE id=?";
        try (var c = dsf.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Nie znaleziono id=" + id);
            }
        } catch (SQLException e) {
            throw SqliteBusyMapper.rethrow(e);
        }
    }

    private static ServicePrice map(ResultSet rs) throws SQLException {
        return new ServicePrice(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("price_cents")
        );
    }
}
