package org.example;

import interfaces.IEnvironment;
import interfaces.IRiverSection;
import java.rmi.RemoteException;

public class EnvironmentImpl implements IEnvironment {
    private final EnvironmentState state;
    private final String name;

    public EnvironmentImpl(String name, EnvironmentState state) {
        this.name = name;
        this.state = state;
        System.out.println("[ENV] EnvironmentImpl created with name: " + name);
    }

    @Override
    public void assignRiverSection(IRiverSection irs) throws RemoteException {
        String rsName = irs.getName();
        System.out.println("[ENV] Assigning river section: " + rsName);
        state.addRiver(rsName, irs);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
