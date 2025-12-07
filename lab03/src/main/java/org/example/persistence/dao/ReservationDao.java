package org.example.persistence.dao;

import org.example.domain.model.Reservation;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationDao {

    Reservation create(Reservation r);

    Optional<Reservation> findById(long id);

    List<Reservation> findByClient(long clientId);

    List<Reservation> findByShopAndDate(long shopId, LocalDate date);

    List<Reservation> findAnonByShopAndDate(long shopId, LocalDate date);

    boolean update(Reservation r, Instant expectedUpdatedAt);

    boolean delete(long id, Instant expectedUpdatedAt);

    List<Reservation> listForEmployeeOnDate(long employeeId, LocalDate date);

    List<Reservation> listForShopOnDate(long shopId, LocalDate date);
}
