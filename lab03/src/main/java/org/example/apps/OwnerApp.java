package org.example.apps;

import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.ValidationException;
import org.example.domain.model.ServicePrice;
import org.example.persistence.db.DataSourceFactory;
import org.example.persistence.db.Migration;
import org.example.service.OwnerService;
import org.example.service.TimeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class OwnerApp {
    private final OwnerService owner;
    private final TimeService time;
    private final DataSourceFactory dsf;

    //pętla dla właściciela
    public OwnerApp(OwnerService owner, TimeService time, DataSourceFactory dsf) {
        this.owner = owner;
        this.time = time;
        this.dsf = dsf;
    }

    public void runOWNER() {
        printHelp();
        var sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = sc.hasNextLine() ? sc.nextLine().trim() : "exit";
            if (line.isBlank()) continue;

            try {
                String[] p = line.split("\\s+");
                String cmd = p[0];

                switch (cmd) {
                    case "help" -> printHelp();
                    case "exit" -> { return; }

                    case "seed" -> {
                        // to lub owner.seedIfEmpty()
                        Migration.seedIfEmpty(dsf);
                        System.out.println("Seed: OK (jeśli było pusto).");
                    }

                    case "cennik-list" -> printCennik();

                    case "cennik-add" -> {
                        if (p.length < 3) { System.out.println("Użycie: cennik-add <nazwa...> <cents>"); break; }
                        int cents = Integer.parseInt(p[p.length - 1]);
                        String name = line.replaceFirst("^cennik-add\\s+", "").replaceFirst("\\s+\\d+$", "");
                        var sp = owner.addService(name, cents);
                        System.out.println("OK. Dodano: id=" + sp.getId());
                    }

                    case "cennik-edit" -> {
                        if (p.length < 3) { System.out.println("Użycie: cennik-edit <id> <cents>"); break; }
                        long id = Long.parseLong(p[1]);
                        int cents = Integer.parseInt(p[2]);
                        owner.updateService(id, cents);
                        System.out.println("OK. Zmieniono cenę.");
                    }

                    case "cennik-del" -> {
                        if (p.length < 2) { System.out.println("Użycie: cennik-del <id>"); break; }
                        long id = Long.parseLong(p[1]);
                        owner.removeService(id);
                        System.out.println("OK. Usunięto pozycję.");
                    }

                    //dodatek
                    case "rez-list" -> {
                        if (p.length < 3) { System.out.println("Użycie: rez-list <shopId> <YYYY-MM-DD>"); break; }
                        long shopId = Long.parseLong(p[1]);
                        LocalDate date = LocalDate.parse(p[2]);
                        owner.allReservationsForDay(shopId, date).forEach(System.out::println);
                    }
                    case "przychod" -> {
                        if (p.length < 4) { System.out.println("Użycie: przychod <shopId> <YYYY-MM-DD> <YYYY-MM-DD>"); break; }
                        long shopId = Long.parseLong(p[1]);
                        LocalDate from = LocalDate.parse(p[2]);
                        LocalDate to   = LocalDate.parse(p[3]);
                        int cents = owner.revenueCents(shopId, from, to);
                        System.out.printf("Przychod %s..%s = %.2f PLN%n", from, to, cents/100.0);
                    }

                    default -> System.out.println("Nieznana komenda. help");
                }
            } catch (ValidationException | NotFoundException ex) {
                System.out.println("BŁĄD: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("BŁĄD: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
    }

    private void printCennik() {
        List<ServicePrice> list = owner.listServices();
        if (list.isEmpty()) { System.out.println("(pusty cennik)"); return; }
        list.forEach(sp -> System.out.printf("- [%d] %s : %.2f PLN%n",
                sp.getId(), sp.getName(), sp.getPriceCents()/100.0));
    }

    private void printHelp() {
        System.out.println("""
            === OwnerApp ===
            Komendy:
              help | exit
              seed
              cennik-list
              cennik-add <nazwa...> <cents>
              cennik-edit <id> <cents>
              cennik-del <id>
              rez-list <shopId> <YYYY-MM-DD>
              Przychod <shopId> <YYYY-MM-DD> <YYYY-MM-DD>
            """);
    }
}
