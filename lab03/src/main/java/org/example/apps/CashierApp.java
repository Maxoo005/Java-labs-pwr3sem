package org.example.apps;

import org.example.apps.util.Cli;
import org.example.domain.model.Reservation;
import org.example.persistence.dao.ReservationDao;
import org.example.service.CashierService;

import java.time.LocalDate;
import java.util.List;

public class CashierApp {
    private final CashierService cashierService;
    private final ReservationDao reservationDao;

    public CashierApp(CashierService cashierService, ReservationDao reservationDao) {
        this.cashierService = cashierService;
        this.reservationDao = reservationDao;
    }
/**
 * Główny punkt wejścia dla trybu CASHIER.
 * Oczekuje pierwszego argumentu jako komendy ("today", "pay", "help"),
 * a kolejne argumenty w formacie klucz–wartość (np. "--shop 1").
 */

    public int runCASHIER(String[] args) {
        if (args.length == 0) { printHelp(); return 0; }
        String cmd = args[0];
        switch (cmd) {
            case "today" -> {
                long shopId = Cli.argLong(args, "--shop", true);
                LocalDate date = Cli.argDate(args, "--date", false, LocalDate.now());
                listForShopAtDate(shopId, date);
                return 0;
            }
            case "pay" -> {
                long reservationId = Cli.argLong(args, "--id", true);
                long shopId = Cli.argLong(args, "--shop", true);
                cashierService.pay(reservationId, shopId);
                System.out.println("OK: opłacono rezerwację id=" + reservationId + " (PAID).");
                return 0;
            }
            case "help" -> {
                printHelp();
                return 0;
            }
            default -> {
                System.err.println("Nieznana komenda: " + cmd);
                printHelp();
                return 2;
            }
        }
    }
    /**
     * Wypisuje rezerwacje dla wskazanego salonu i daty.
     * Używa ReservationDao wyłącznie do odczytu
     * Jeśli toString() nie jest czytelny, sformatowane wypisywanie pól.
     */
    private void listForShopAtDate(long shopId, LocalDate date) {
        List<Reservation> rows = reservationDao.listForShopOnDate(shopId, date);
        if (rows.isEmpty()) {
            System.out.println("Brak rezerwacji dla shopId=" + shopId + " w dniu " + date + ".");
            return;
        }
        rows.forEach(System.out::println);
    }

    private void printHelp() {
        System.out.println("""
            Komendy:
              help
              today --shop <id> [--date <YYYY-MM-DD>]
              pay --id <reservationId> --shop <id>
        """);
    }
}
