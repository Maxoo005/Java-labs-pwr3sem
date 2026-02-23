package org.example;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import java.rmi.RemoteException;

public class RiverSectionImpl implements IRiverSection {
    private final RiverState state;
    private final String name;

    public RiverSectionImpl(String name, RiverState state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public void setRealDischarge(int realDischarge) throws RemoteException {
        state.setUpstreamDischarge(realDischarge);
    }

    @Override
    public void setRainfall(int rainfall) throws RemoteException {
        state.setRainfall(rainfall);
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb) throws RemoteException {
        state.setDownstreamBasin(irb);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
