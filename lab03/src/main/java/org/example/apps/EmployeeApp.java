package org.example.apps;

import org.example.apps.util.Cli;
import org.example.domain.model.Reservation;
import org.example.persistence.dao.ReservationDao;
import org.example.service.EmployeeService;

import java.time.LocalDate;
import java.util.List;

public class EmployeeApp {
    private final EmployeeService employeeService;
    private final ReservationDao reservationDao;

    public EmployeeApp(EmployeeService employeeService, ReservationDao reservationDao) {
        this.employeeService = employeeService;
        this.reservationDao = reservationDao;
    }

    public int runEMPLOYEER(String[] args) {
        if (args.length == 0) { printHelp(); return 0; }
        String cmd = args[0];
        switch (cmd) {
            case "my-today" -> {
                long employeeId = Cli.argLong(args, "--employee", true);
                LocalDate date = LocalDate.now();
                List<Reservation> list = reservationDao.listForEmployeeOnDate(employeeId, date);

                if (list.isEmpty()) {
                    System.out.println("Brak rezerwacji dla pracownika id=" + employeeId + " w dniu " + date + ".");
                    return 0;
                }

                list.forEach(System.out::println);
                return 0;
            }
            case "start" -> {
                long reservationId = Cli.argLong(args, "--id", true);
                long employeeId = Cli.argLong(args, "--employee", true);
                employeeService.start(reservationId, employeeId);
                System.out.println("OK: rozpoczęto rezerwację id=" + reservationId + " (IN_PROGRESS).");
                return 0;
            }
            case "finish" -> {
                long reservationId = Cli.argLong(args, "--id", true);
                long employeeId = Cli.argLong(args, "--employee", true);
                employeeService.finish(reservationId, employeeId);
                System.out.println("OK: zakończono rezerwację id=" + reservationId + " (DONE).");
                return 0;
            }
            case "help" -> { printHelp(); return 0; }
            default -> {
                System.err.println("Nieznana komenda: " + cmd);
                printHelp();
                return 2;
            }
        }
    }

    private void printHelp() {
        System.out.println("""
            Komendy:
              help
              my-today --employee <id>
              start --id <reservationId> --employee <id>
              finish --id <reservationId> --employee <id>
        """);
    }
}
