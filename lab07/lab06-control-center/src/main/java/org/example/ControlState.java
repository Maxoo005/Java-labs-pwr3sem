package org.example;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // CopyOnWriteArrayList jest bezpieczna przy jednoczesnym czytaniu i pisaniu

public class ControlState {
    private final List<BasinInfo> registeredBasins = new CopyOnWriteArrayList<>();

    public void addBasin(interfaces.IRetensionBasin stub) {
        // ID
        int id = registeredBasins.size() + 1;
        String name = "Unknown";
        try {
            name = stub.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BasinInfo basin = new BasinInfo(id, name, stub);
        registeredBasins.add(basin);
        System.out.println("[CENTRALA] Zarejestrowano nowy zbiornik: " + basin);
    }

    public List<BasinInfo> getBasins() {
        return registeredBasins;
    }
}
