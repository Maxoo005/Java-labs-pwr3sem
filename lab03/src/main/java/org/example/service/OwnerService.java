package org.example.service;

import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.ValidationException;
import org.example.domain.model.Reservation;
import org.example.domain.model.ReservationStatus;
import org.example.domain.model.ServicePrice;
import org.example.persistence.dao.ReservationDao;
import org.example.persistence.dao.ServicePriceDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class OwnerService {

    private final ServicePriceDao servicePriceDao;
    private final ReservationDao reservationDao; // może być null


    //INNY konstruktor
    public OwnerService(ServicePriceDao servicePriceDao, ReservationDao reservationDao) {
        this.servicePriceDao = Objects.requireNonNull(servicePriceDao);
        this.reservationDao = Objects.requireNonNull(reservationDao);
    }

    //dodatkowwa usługa po walidacji
    public ServicePrice addService(String name, int priceCents) {
        validateName(name);
        validatePrice(priceCents);
        boolean exists = servicePriceDao.findAll().stream()
                .anyMatch(sp -> sp.getName().equalsIgnoreCase(name.trim()));
        if (exists) throw new ValidationException("Usługa o nazwie '" + name + "' już istnieje.");
        return servicePriceDao.create(ServicePrice.ofNew(name.trim(), priceCents));
    }

    // Lista wszystkich pozycji cennika.
    public List<ServicePrice> listServices() { return servicePriceDao.findAll(); }

    // Zmiana ceny istniejącej pozycji
    public ServicePrice updateService(long id, int newPriceCents) {
        validatePrice(newPriceCents);
        var sp = servicePriceDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono usługi id=" + id));
        sp.setPriceCents(newPriceCents);
        servicePriceDao.update(sp);
        return sp;
    }

    // Usunięcie pozycji cennika.
    public void removeService(long id) {
        var sp = servicePriceDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono usługi id=" + id));
        servicePriceDao.delete(sp.getId());
    }

    // Rezerwacje dla salonu w konkretnym dniu
    public List<Reservation> allReservationsForDay(long shopId, LocalDate date) {
        ensureRezDao();
        return reservationDao.findByShopAndDate(shopId, date);
    }

    //Suma przychodu (PAID) w groszach w podanym zakresie dat
    public int revenueCents(long shopId, LocalDate from, LocalDate to) {
        ensureRezDao();
        if (to.isBefore(from)) throw new ValidationException("Zakres dat jest pusty.");
        int sum = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            for (var r : reservationDao.findByShopAndDate(shopId, d)) {
                if (r.getStatus() == ReservationStatus.PAID) sum += r.getPriceCents();
            }
        }
        return sum;
    }
    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new ValidationException("Nazwa usługi nie może być pusta.");
        if (name.length() > 80)
            throw new ValidationException("Zbyt długa nazwa (max 80 znaków).");
    }

    private static void validatePrice(int cents) {
        if (cents < 0) throw new ValidationException("Cena nie może być ujemna.");
    }

    private void ensureRezDao() {
        if (reservationDao == null)
            throw new IllegalStateException("(użyj konstruktora 2-argumentowego).");
    }
}
