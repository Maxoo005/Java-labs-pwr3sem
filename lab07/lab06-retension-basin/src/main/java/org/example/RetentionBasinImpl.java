package org.example;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import java.rmi.RemoteException;

public class RetentionBasinImpl implements IRetensionBasin {
    private final BasinState state;
    private final String name;

    public RetentionBasinImpl(String name, BasinState state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public int getWaterDischarge() throws RemoteException {
        return state.getRealDischarge();
    }

    @Override
    public long getFillingPercentage() throws RemoteException {
        return state.getFillingPercentage();
    }

    @Override
    public void setWaterDischarge(int waterDischarge) throws RemoteException {
        state.setTargetDischarge(waterDischarge);
    }

    @Override
    public void setWaterInflow(int waterInflow, IRiverSection irs) throws RemoteException {
        try {
            state.setInflow(irs.getName(), waterInflow);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void assignRiverSection(IRiverSection irs) throws RemoteException {
        state.setRiverOut(irs);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
