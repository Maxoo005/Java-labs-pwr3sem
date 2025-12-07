package org.example.service;

import org.example.domain.exception.ConcurrentUpdateException;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.ValidationException;
import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationStatus;
import org.example.persistence.dao.ReservationDao;

import java.time.Instant;
import java.util.Objects;

public class EmployeeService {

    private final ReservationDao reservationDao;

    public EmployeeService(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    /**
     * Employee rozpoczyna usługę.
     */
    public void start(long reservationId, long employeeId) {
        Reservation rez = reservationDao.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono rezerwacji id=" + reservationId));

        // Weryfikacja przypisania pracownika
        if (!Objects.equals(rez.getEmployeeId(), employeeId)) {
            throw new ValidationException("Rezerwacja nie jest przypisana do pracownika id=" + employeeId);
        }

        if (rez.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ValidationException("Start możliwy tylko dla statusu CONFIRMED (aktualnie: " + rez.getStatus() + ")");
        }

        var expected = rez.getUpdatedAtUtc();
        rez.setStatus(ReservationStatus.IN_PROGRESS);
        rez.setUpdatedAtUtc(Instant.now());

        if (!reservationDao.update(rez, expected)) {
            throw new ConcurrentUpdateException("Rezerwacja została zmieniona równolegle – spróbuj ponownie.");
        }
    }

    /**
     * Employee kończy usługę.
     * Dozwolone przejście: IN_PROGRESS -> DONE
     */
    public void finish(long reservationId, long employeeId) {
        Reservation rez = reservationDao.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono rezerwacji id=" + reservationId));

        if (!Objects.equals(rez.getEmployeeId(), employeeId)) {
            throw new ValidationException("Rezerwacja nie jest przypisana do pracownika id=" + employeeId);
        }

        if (rez.getStatus() != ReservationStatus.IN_PROGRESS) {
            throw new ValidationException("Zakończenie możliwe tylko dla statusu IN_PROGRESS (aktualnie: " + rez.getStatus() + ")");
        }

        var expected = rez.getUpdatedAtUtc();
        rez.setStatus(ReservationStatus.DONE);
        rez.setUpdatedAtUtc(Instant.now());

        if (!reservationDao.update(rez, expected)) {
            throw new ConcurrentUpdateException("Rezerwacja została zmieniona równolegle – spróbuj ponownie.");
        }
    }
}
