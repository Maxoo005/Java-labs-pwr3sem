package org.example;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import java.rmi.RemoteException;

public class ControlCenterImpl implements IControlCenter {
    private final ControlState state;
    private final String name;

    public ControlCenterImpl(String name, ControlState state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb) throws RemoteException {
        state.addBasin(irb);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
