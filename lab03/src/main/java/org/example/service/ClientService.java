package org.example.service;

import org.example.domain.exception.ConcurrentUpdateException;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.ValidationException;
import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationStatus;
import org.example.persistence.dao.ReservationDao;
import org.example.persistence.dao.ServicePriceDao;
import org.example.domain.model.ServicePrice;

import java.time.*;
import java.util.List;
import java.util.Objects;

public class ClientService {

    private final ReservationDao reservationDao;
    private final ServicePriceDao servicePriceDao;

    public ClientService(ReservationDao reservationDao, ServicePriceDao servicePriceDao) {
        this.reservationDao = Objects.requireNonNull(reservationDao);
        this.servicePriceDao = Objects.requireNonNull(servicePriceDao);
    }

    //Utworzenie rezerwacji z ceną z cennika
    public Reservation makeReservation(long shopId, long clientId, String serviceName,
                                       LocalDate date, LocalTime time) {

        if (serviceName == null || serviceName.isBlank())
            throw new ValidationException("Nazwa usługi nie może być pusta.");
        if (date == null || time == null)
            throw new ValidationException("Data i czas są wymagane.");

        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        boolean inPast = date.isBefore(today) || (date.isEqual(today) && !time.isAfter(nowTime));
        if (inPast)
            throw new ValidationException("Termin rezerwacji musi być w przyszłości (data/godzina).");

        int priceCents = servicePriceDao.findAll().stream()
                .filter(sp -> sp.getName() != null && sp.getName().equalsIgnoreCase(serviceName))
                .map(ServicePrice::getPriceCents)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Brak usługi w cenniku: " + serviceName));

        var now = Instant.now();
        var r = new Reservation(
                null,
                shopId,
                serviceName.trim(),
                clientId,
                null,
                date,
                time,
                ReservationStatus.NEW,
                now,
                now,
                priceCents
        );
        return reservationDao.create(r);
    }

    /** Moje rezerwacje. */
    public List<Reservation> myReservations(long clientId) {
        return reservationDao.findByClient(clientId);
    }

    /** Anulowanie. */
    public void cancelReservation(long reservationId, long clientId) {
        var rez = reservationDao.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono rezerwacji #" + reservationId));

        if (rez.getClientId() == null || rez.getClientId() != clientId)
            throw new ValidationException("Nie możesz anulować cudzej rezerwacji.");

        // enum NEW, IN_PROGRESS, DONE, PAID, NO_SHOW, CANCELED
        switch (rez.getStatus()) {
            case CANCELED -> throw new ValidationException("Rezerwacja już anulowana.");
            case DONE, PAID, NO_SHOW -> throw new ValidationException("Nie można anulować zakończonej/rozliczonej rezerwacji.");
            case NEW, CONFIRMED, IN_PROGRESS -> { /* OK, można anulować */ }
        }

        var expected = rez.getUpdatedAtUtc();   // stary znacznik
        rez.setStatus(ReservationStatus.CANCELED);
        rez.setUpdatedAtUtc(Instant.now());     // nowy znacznik

        if (!reservationDao.update(rez, expected)) {
            throw new ConcurrentUpdateException("Rezerwacja została zmieniona równolegle – spróbuj ponownie.");
        }
    }

}
