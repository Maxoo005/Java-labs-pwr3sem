package org.example;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import java.rmi.RemoteException;

public class SeaImpl implements IRetensionBasin {
    private final String name;
    private long totalWater = 0;

    public SeaImpl(String name) {
        this.name = name;
    }

    @Override
    public synchronized void setWaterInflow(int waterInflow, IRiverSection irs) throws RemoteException {
        if (waterInflow > 0) {
            totalWater += waterInflow;
            String sourceName = "Unknown";
            try {
                sourceName = irs.getName();
            } catch (Exception e) {
            }
            System.out.println(
                    "[MORZE] Wpada woda z " + sourceName + ": " + waterInflow + " m3/s | Razem: " + totalWater + " m3");
        }
    }

    @Override
    public int getWaterDischarge() throws RemoteException {
        return 0; // Morze nie oddaje wody
    }

    @Override
    public long getFillingPercentage() throws RemoteException {
        return 0; // Morze jest nieskończone
    }

    @Override
    public void setWaterDischarge(int waterDischarge) throws RemoteException {
        // Ignoruj
    }

    @Override
    public void assignRiverSection(IRiverSection irs) throws RemoteException {
        // Ignoruj - Morze jest końcem
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
