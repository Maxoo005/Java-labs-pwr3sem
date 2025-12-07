package org.example.apps;

import org.example.apps.config.AppConfig;
import org.example.domain.exception.FileLockedOrBusyException;
import org.example.persistence.dao.ReservationDao;
import org.example.persistence.dao.ServicePriceDao;
import org.example.persistence.dao.sqlite.ReservationDaoSqlite;
import org.example.persistence.dao.sqlite.ServicePriceDaoSqlite;
import org.example.persistence.db.DataSourceFactory;
import org.example.persistence.db.Migration;
import org.example.service.CashierService;
import org.example.service.ClientService;
import org.example.service.EmployeeService;
import org.example.service.OwnerService;
import org.example.service.TimeService;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainLauncher {

    public static void main(String[] args) {
        System.out.println("=== Barbershop – CLI ===");

        // 1) Konfiguracja + DB
        AppConfig cfg = new AppConfig();
        DataSourceFactory dsf = new DataSourceFactory(cfg);
        Migration.ensure(cfg, dsf);

        // 2) DAO (jeden zestaw na cały program)
        ServicePriceDao servicePriceDao = new ServicePriceDaoSqlite(dsf);
        ReservationDao reservationDao  = new ReservationDaoSqlite(dsf);

        // 3) Serwisy
        OwnerService ownerService     = new OwnerService(servicePriceDao, reservationDao);
        ClientService clientService   = new ClientService(reservationDao, servicePriceDao);
        EmployeeService employeeService = new EmployeeService(reservationDao);
        CashierService cashierService   = new CashierService(reservationDao);

        // 4) Czas wirtualny
        int tickMinutes = Integer.parseInt(cfg.getOrDefault("time.tick.minutes", "15"));
        TimeService timeService = new TimeService(dsf, tickMinutes);

        // 5) Tryb bezargumentowy = menu ról
        if (args.length == 0) {
            runRoleMenu(ownerService, clientService, employeeService, cashierService,
                    timeService, reservationDao, servicePriceDao, dsf);
            return;
        }

        // 6) Tryb jednorazowej komendy: java ... MainLauncher <rola> [komenda...]
        String role = args[0].toLowerCase(Locale.ROOT);
        String[] tail = Arrays.copyOfRange(args, 1, args.length);

        try {
            switch (role) {
                case "owner" -> new OwnerApp(ownerService, timeService, dsf).runOWNER();
                case "client" -> new ClientApp(clientService, servicePriceDao).runInteractive();
                case "employee" -> new EmployeeApp(employeeService, reservationDao).runEMPLOYEER(tail);
                case "cashier" -> new CashierApp(cashierService, reservationDao).runCASHIER(tail);
                default -> System.out.println("Nieznana rola. Użyj: owner | client | employee | cashier");
            }
        } catch (FileLockedOrBusyException ex) {
            System.out.println("BŁĄD BAZY: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("BŁĄD: " + ex.getClass().getSimpleName() + " – " + ex.getMessage());
        }
    }

    //MENU główne

    private static void runRoleMenu(OwnerService ownerService,
                                    ClientService clientService,
                                    EmployeeService employeeService,
                                    CashierService cashierService,
                                    TimeService timeService,
                                    ReservationDao reservationDao,
                                    ServicePriceDao servicePriceDao,
                                    DataSourceFactory dsf) {

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("""
                Wybierz rolę (lub 'exit'):
                  1) owner     – właściciel
                  2) client    – klient
                  3) employee  – pracownik
                  4) cashier   – kasjer
                """);
            System.out.print("> ");
            String pick = sc.hasNextLine() ? sc.nextLine().trim().toLowerCase(Locale.ROOT) : "exit";
            if (pick.equals("exit")) {
                System.out.println("Koniec programu.");
                return;
            }

            try {
                switch (pick) {
                    case "1", "owner" -> {
                        // OwnerApp ma własną pętlę wewnątrz runOWNER()
                        new OwnerApp(ownerService, timeService, dsf).runOWNER();
                        // po 'exit' z OwnerApp wracamy tutaj do menu
                    }
                    case "2", "client" -> {
                        // ClientApp ma własną pętlę runInteractive()
                        new ClientApp(clientService, servicePriceDao).runInteractive();
                        // po 'exit' z ClientApp wracamy tutaj do menu
                    }
                    case "3", "employee" -> {
                        runEmployeeShell(employeeService, reservationDao, sc);
                        // po 'back' wracamy do menu ról
                    }
                    case "4", "cashier" -> {
                        runCashierShell(cashierService, reservationDao, sc);
                        // po 'back' wracamy do menu ról
                    }
                    default -> System.out.println("Nieznana opcja. Wpisz 1/2/3/4 lub 'exit'.");
                }
            } catch (FileLockedOrBusyException ex) {
                System.out.println("BŁĄD BAZY: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("BŁĄD: " + ex.getClass().getSimpleName() + " – " + ex.getMessage());
            }
        }
    }

    //SHELL EMPLOYEE

    private static void runEmployeeShell(EmployeeService employeeService,
                                         ReservationDao reservationDao,
                                         Scanner sc) {
        EmployeeApp app = new EmployeeApp(employeeService, reservationDao);
        System.out.println("""
            === Tryb EMPLOYEE ===
            Wpisuj polecenia (albo 'back' by wrócić do menu ról):
              my-today --employee <id>
              start --id <reservationId> --employee <id>
              finish --id <reservationId> --employee <id>
            """);
        while (true) {
            System.out.print("(employee)> ");
            String line = sc.hasNextLine() ? sc.nextLine().trim() : "back";
            if (line.isBlank()) continue;
            if (line.equalsIgnoreCase("back") || line.equalsIgnoreCase("exit")) {
                // NIE kończymy programu – tylko wychodzimy z trybu employee do menu ról
                break;
            }
            String[] argv = splitArgs(line);
            app.runEMPLOYEER(argv);
        }
    }

    //SHELL CASHIER

    private static void runCashierShell(CashierService cashierService,
                                        ReservationDao reservationDao,
                                        Scanner sc) {
        CashierApp app = new CashierApp(cashierService, reservationDao);
        System.out.println("""
            === Tryb CASHIER ===
            Wpisuj polecenia (albo 'back' by wrócić do menu ról):
              today --shop <id> [--date YYYY-MM-DD]
              pay --id <reservationId> --shop <id>
            """);
        while (true) {
            System.out.print("(cashier)> ");
            String line = sc.hasNextLine() ? sc.nextLine().trim() : "back";
            if (line.isBlank()) continue;
            if (line.equalsIgnoreCase("back") || line.equalsIgnoreCase("exit")) {
                // NIE kończymy programu – tylko wychodzimy z trybu cashier do menu ról
                break;
            }
            String[] argv = splitArgs(line);
            app.runCASHIER(argv);
        }
    }

    //WSPÓLNY SPLITTER ARGUMENTÓW

    /**
     * Rozbija linię na argumenty, wspiera cudzysłowy:
     *  "strzyżenie męskie" -> jeden argument.
     */
    private static String[] splitArgs(String line) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher m = pattern.matcher(line);
        java.util.List<String> list = new java.util.ArrayList<>();
        while (m.find()) {
            String g1 = m.group(1);
            String g2 = m.group(2);
            list.add(g1 != null ? g1 : g2);
        }
        return list.toArray(new String[0]);
    }
}
