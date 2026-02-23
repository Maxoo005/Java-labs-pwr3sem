package org.example;

import java.util.HashMap;
import java.util.Map;

public class BasinState {
    private final int maxVolume;
    private long currentVolume; // long, bo woda w m3 może być duża

    // Ustawienia sterowania
    private int targetDischarge = 0; // Ile operator chce wypuścić (swd)

    // Rzeczywistość
    private int realDischarge = 0; // Ile faktycznie wypłynęło

    // Napływy
    private final Map<String, Integer> inflows = new HashMap<>();

    // Wyjście
    private interfaces.IRiverSection riverOut = null;

    public BasinState(int maxVolume, int startVolume) {
        this.maxVolume = maxVolume;
        this.currentVolume = startVolume;
    }

    // Settery
    public synchronized void setTargetDischarge(int targetDischarge) {
        this.targetDischarge = targetDischarge;
        System.out.println("[BASIN] Operator ustawił zrzut na: " + targetDischarge);
    }

    public synchronized void setInflow(String riverName, int value) {
        inflows.put(riverName, value);
    }

    public synchronized void setRiverOut(interfaces.IRiverSection stub) {
        this.riverOut = stub;
        System.out.println("[BASIN] Podłączono rzekę wyjściową (RMI)");
    }

    // Fizyka (Tick)

    public synchronized void tick() {
        // suma napływów ze wszystkich rzek
        int totalInflow = inflows.values().stream().mapToInt(Integer::intValue).sum();

        // bilans (dt = 1 sekunda)
        // chcemy wypuścić targetDischarge
        int potentialOutflow = targetDischarge;
        long nextVolume = currentVolume + totalInflow - potentialOutflow;

        // Obsługa warunków brzegowych

        if (nextVolume > maxVolume) {
            // PRZEPEŁNIENIE
            // wylewa się inflow (wylewamy wszystko co wpada + nadmiar)
            realDischarge = totalInflow;
            // Ale musimy też uwzględnić, że zbiornik jest pełny pod korek
            currentVolume = maxVolume;
            System.out.println("[ALARM] PRZEPEŁNIENIE! Zrzut awaryjny: " + realDischarge);

        } else if (nextVolume < 0) {
            // SUSZA
            // Wypływa wszystko co było + to co wpadło
            realDischarge = (int) currentVolume + totalInflow;
            currentVolume = 0;
            System.out.println("[ALARM] ZBIORNIK PUSTY! Brak wody do zrzutu.");

        } else {
            // WARUNKI NORMALNE
            realDischarge = potentialOutflow;
            currentVolume = nextVolume;
        }
    }

    // Gettery
    public synchronized int getRealDischarge() {
        return realDischarge;
    }

    public synchronized int getTargetDischarge() {
        return targetDischarge;
    }

    public synchronized long getFillingPercentage() {
        if (maxVolume == 0)
            return 0;
        return (currentVolume * 100) / maxVolume;
    }

    public synchronized interfaces.IRiverSection getRiverOut() {
        return riverOut;
    }
}
