package org.example.apps;

import org.example.domain.model.ServicePrice;
import org.example.persistence.dao.ServicePriceDao;
import org.example.service.ClientService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClientApp
 *    - cennik
 *    - rez-make --shop <id> --client <id> --service <ID|NAZWA...> --date <YYYY-MM-DD> --time <HH:mm>
 *    - rez-my --client <id>
 *    - rez-cancel --id <reservationId> --client <id>
 */
public class ClientApp {
    private final ClientService clientService;
    private final ServicePriceDao servicePriceDao;

    public ClientApp(ClientService clientService, ServicePriceDao servicePriceDao) {
        this.clientService = clientService;
        this.servicePriceDao = servicePriceDao;
    }

    private static String formatPricePLN(long cents) {
        long zl = cents / 100;
        long gr = Math.abs(cents % 100);
        return String.format("%d,%02d zł", zl, gr);
    }

    /** Tryb interaktywn. */
    public void runInteractive() {
        Scanner sc = new Scanner(System.in);  // NIE zamykam System.in
        System.out.println("=== ClientApp ===");
        printHelp();

        while (true) {
            System.out.print("> ");

            if (!sc.hasNextLine()) break; // jeśli IDE zamknie wejście, kończymy tylko ClientApp

            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            // wracanie do menu ról
            if ("back".equalsIgnoreCase(line)) {
                return;
            }

            // stare wyjście
            if ("exit".equalsIgnoreCase(line)) {
                return;
            }

            if ("help".equalsIgnoreCase(line)) {
                printHelp();
                continue;
            }

            String[] args = splitKeepingQuotes(line);
            String cmd = args[0];

            try {
                switch (cmd) {
                    case "cennik" ->
                            cmdCennik();
                    case "rez-make" ->
                            cmdRezMake(Arrays.copyOfRange(args, 1, args.length));
                    case "rez-my" ->
                            cmdRezMy(Arrays.copyOfRange(args, 1, args.length));
                    case "rez-cancel" ->
                            cmdRezCancel(Arrays.copyOfRange(args, 1, args.length));
                    default ->
                            System.out.println("Nieznana komenda. help");
                }

            } catch (IllegalArgumentException ex) {
                System.out.println("BŁĄD: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("BŁĄD (nieoczekiwany): " +
                        ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
    }
    //cennik usług
    private void cmdCennik() {
        List<ServicePrice> list = servicePriceDao.findAll();
        if (list.isEmpty()) {
            System.out.println("(pusty cennik)");
            return;
        }
        for (ServicePrice sp : list) {
            System.out.printf("%d | %-20s | %s%n",
                    sp.getId(),
                    sp.getName(),
                    formatPricePLN(sp.getPriceCents()));
        }
    }
    //tworzenie rezerwacji, obsługa
    private void cmdRezMake(String[] rest) {
        if (rest.length == 0) throw new IllegalArgumentException("Użycie: rez-make --shop <id> --client <id> --service <nazwa|id> --date <YYYY-MM-DD> --time <HH:mm>\n       lub: rez-make <shopId> <clientId> <serviceName...> <YYYY-MM-DD> <HH:mm>");


        Map<String, String> flags = parseFlags(rest);
        Long shopId = tryParseLong(flags.get("--shop"));
        Long clientId = tryParseLong(flags.get("--client"));
        String service = flags.get("--service");
        LocalDate date = tryParseDate(flags.get("--date"));
        LocalTime time = tryParseTime(flags.get("--time"));

        boolean haveFlagSyntax = shopId != null || clientId != null || service != null || date != null || time != null;

        if (haveFlagSyntax) {
            // Wymagane wszystkie
            if (shopId == null) throw new IllegalArgumentException("Brak: --shop <id>");
            if (clientId == null) throw new IllegalArgumentException("Brak: --client <id>");
            if (service == null || service.isBlank()) throw new IllegalArgumentException("Brak: --service <ID|NAZWA...>");
            if (date == null) throw new IllegalArgumentException("Brak: --date <YYYY-MM-DD>");
            if (time == null) throw new IllegalArgumentException("Brak: --time <HH:mm>");
            doMake(shopId, clientId, service, date, time);
            return;
        }

        // Składnia pozycyjna
        if (rest.length < 5)
            throw new IllegalArgumentException("Za mało argumentów. Oczekiwano: rez-make <shopId> <clientId> <serviceName...> <YYYY-MM-DD> <HH:mm>");
        long sId = parseLongStrict(rest[0], "shopId");
        long cId = parseLongStrict(rest[1], "clientId");

        // data to ostatni element-1
        LocalDate d = parseDateStrict(rest[rest.length - 2], "data");
        LocalTime t = parseTimeStrict(rest[rest.length - 1], "czas");

        // nazwa usługi = wszystko pomiędzy indeksami 2..len-3
        String serviceName = String.join(" ", Arrays.asList(rest).subList(2, rest.length - 2));
        if (serviceName.isBlank()) throw new IllegalArgumentException("Nazwa usługi jest pusta.");
        doMake(sId, cId, serviceName, d, t);
    }
    //własciwe twaorzenie rezerwacji
    private void doMake(long shopId, long clientId, String service, LocalDate date, LocalTime time) {
        String serviceName = service;
        if (service != null && service.matches("\\d+")) {
            long id = Long.parseLong(service);
            Optional<ServicePrice> sp = servicePriceDao.findById(id);
            if (sp.isEmpty()) throw new IllegalArgumentException("Brak usługi o id=" + id + " w cenniku.");
            serviceName = sp.get().getName();
        }
        long newId = clientService
                .makeReservation(shopId, clientId, serviceName, date, time)
                .getId();
        System.out.println("OK: utworzono rezerwację id=" + newId);
    }

    //lista rezerwacji klienta
    private void cmdRezMy(String[] rest) {
        Map<String, String> flags = parseFlags(rest);
        Long clientId = tryParseLong(flags.get("--client"));
        if (clientId == null) {
            // składnia pozycyjna: rez-my <clientId>
            if (rest.length < 1) throw new IllegalArgumentException("Użycie: rez-my --client <id>  lub  rez-my <clientId>");
            clientId = parseLongStrict(rest[0], "clientId");
        }
        var list = clientService.myReservations(clientId);
        if (list.isEmpty()) { System.out.println("(brak rezerwacji)"); return; }
        list.forEach(System.out::println);
    }
    //anulacja
    private void cmdRezCancel(String[] rest) {
        Map<String, String> flags = parseFlags(rest);
        Long id = tryParseLong(flags.get("--id"));
        Long clientId = tryParseLong(flags.get("--client"));

        if (id == null || clientId == null) {
            // składnia pozycyjna
            if (rest.length < 2) throw new IllegalArgumentException("Użycie: rez-cancel --id <reservationId> --client <id>  lub  rez-cancel <reservationId> <clientId>");
            id = parseLongStrict(rest[0], "reservationId");
            clientId = parseLongStrict(rest[1], "clientId");
        }

        clientService.cancelReservation(id, clientId);
        System.out.println("OK: anulowano.");
    }

    //Prosty parser --key value; ignoruje wartości bez pary.
    private static Map<String, String> parseFlags(String[] args) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < args.length - 1; i++) {
            String a = args[i];
            if (a.startsWith("--")) m.put(a, args[i + 1]);
        }
        return m;
    }

    private static Long tryParseLong(String s) {
        if (s == null) return null;
        try { return Long.parseLong(s); } catch (NumberFormatException ignored) { return null; }
    }
    private static long parseLongStrict(String s, String name) {
        try { return Long.parseLong(s); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(name + " musi być liczbą całkowitą, otrzymano: " + s); }
    }
    private static LocalDate tryParseDate(String s) {
        if (s == null) return null;
        try { return LocalDate.parse(s); } catch (Exception ignored) { return null; }
    }
    private static LocalTime tryParseTime(String s) {
        if (s == null) return null;
        try { return LocalTime.parse(s); } catch (Exception ignored) { return null; }
    }
    private static LocalDate parseDateStrict(String s, String name) {
        try { return LocalDate.parse(s); } catch (Exception ex) { throw new IllegalArgumentException(name + " w formacie YYYY-MM-DD, otrzymano: " + s); }
    }
    private static LocalTime parseTimeStrict(String s, String name) {
        try { return LocalTime.parse(s); } catch (Exception ex) { throw new IllegalArgumentException(name + " w formacie HH:mm, otrzymano: " + s); }
    }

    //Dzieli linię wejścia na tokeny, przy czym fragmenty w cudzysłowie
    private static String[] splitKeepingQuotes(String line) {
        List<String> out = new ArrayList<>();
        Matcher m = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(line);
        while (m.find()) {
            if (m.group(1) != null) out.add(m.group(1));
            else out.add(m.group(2));
        }
        return out.toArray(new String[0]);
    }

    private static void printHelp() {
        System.out.println("""
            Komendy:
              help | exit
              cennik
              rez-make --shop <id> --client <id> --service <ID|NAZWA...> --date <YYYY-MM-DD> --time <HH:mm>
              rez-my --client <id>
              rez-cancel --id <reservationId> --client <id>
        """);
    }
}
