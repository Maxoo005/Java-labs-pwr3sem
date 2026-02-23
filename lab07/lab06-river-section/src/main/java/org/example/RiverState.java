package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class RiverState {
    private final int myPort;
    private final int delay; // Opóźnienie

    // Stan wejścia
    private int currentRainfall = 0; // Deszcz (srf)
    private int upstreamDischarge = 0; // Zrzut ze zbiornika powyżej (srd)

    // Stan rzeki
    private final Queue<Integer> waterFlowQueue = new LinkedList<>();

    // Stan wyjścia
    private interfaces.IRetensionBasin downstreamBasin = null;

    public RiverState(int myPort, int delay) {
        this.myPort = myPort;
        this.delay = delay;
        // Wypełniamy rzekę zerami na start, żeby miała odpowiednią "długość"
        for (int i = 0; i < delay; i++) {
            waterFlowQueue.add(0);
        }
    }

    // Metody ustawiane przez sieć (Thread-safe)
    public synchronized void setRainfall(int rainfall) {
        this.currentRainfall = rainfall;
        System.out.println("[RIVER] Aktualny deszcz: " + rainfall);
    }

    public synchronized void setUpstreamDischarge(int discharge) {
        this.upstreamDischarge = discharge;
        System.out.println("[RIVER] Napływ ze zbiornika powyżej: " + discharge);
    }

    public synchronized int getCurrentRainfall() {
        return currentRainfall;
    }

    public synchronized int getUpstreamDischarge() {
        return upstreamDischarge;
    }

    public synchronized void setDownstreamBasin(interfaces.IRetensionBasin stub) {
        this.downstreamBasin = stub;
        System.out.println("[RIVER] Ustawiono ujście do zbiornika (RMI)");
    }

    // Krok symulacji (wywoływany co sekundę)

    // Oblicza bilans wody w rzece.
    // @return Wartość, która wypływa z rzeki w tym kroku.
    public synchronized int tick() {
        int totalInflow = upstreamDischarge + currentRainfall;

        // wlot rzeki
        waterFlowQueue.add(totalInflow);

        // Pobieramy z początku kolejki (wylot rzeki
        Integer outflow = waterFlowQueue.poll();

        if (outflow == null)
            outflow = 0;

        return outflow;
    }

    // Getters
    public synchronized interfaces.IRetensionBasin getDownstreamBasin() {
        return downstreamBasin;
    }

    public int getMyPort() {
        return myPort;
    }
}
