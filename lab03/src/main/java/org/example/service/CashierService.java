package org.example.service;

import org.example.domain.exception.ConcurrentUpdateException;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.ValidationException;
import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationStatus;
import org.example.persistence.dao.ReservationDao;

import java.time.Instant;

public class CashierService {

    private final ReservationDao reservationDao;

    public CashierService(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    /**
     * Kasjer rozlicza usługę.
     * Dozwolone przejście: DONE -> PAID
     * Weryfikacje: reservation istnieje, shopId zgodny, status prawidłowy.
     */
    public void pay(long reservationId, long shopId) {
        Reservation rez = reservationDao.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono rezerwacji id=" + reservationId));

        if (rez.getShopId() != shopId) {
            throw new ValidationException("Rezerwacja należy do innego zakładu (shopId=" + rez.getShopId() + ")");
        }

        if (rez.getStatus() != ReservationStatus.DONE) {
            throw new ValidationException("Płatność możliwa tylko dla statusu DONE (aktualnie: " + rez.getStatus() + ")");
        }

        var expected = rez.getUpdatedAtUtc();
        rez.setStatus(ReservationStatus.PAID);
        rez.setUpdatedAtUtc(Instant.now());

        if (!reservationDao.update(rez, expected)) {
            throw new ConcurrentUpdateException("Rezerwacja została zmieniona równolegle – spróbuj ponownie.");
        }
    }
}
