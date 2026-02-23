package org.example;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnvironmentState {
    // Lista bezpieczna wątkowo- Serwer dodaje- UI czyta
    private final List<RiverInfo> rivers = new CopyOnWriteArrayList<>();

    public void addRiver(String name, interfaces.IRiverSection stub) {
        // Sprawdzamy, czy już takiej nie ma, żeby nie dublować
        for (RiverInfo r : rivers) {
            if (r.name().equals(name))
                return;
        }

        RiverInfo river = new RiverInfo(name, stub);
        rivers.add(river);
        System.out.println("[ENV] Zarejestrowano nowy odcinek rzeczny: " + river);
    }

    public List<RiverInfo> getRivers() {
        return rivers;
    }
}
