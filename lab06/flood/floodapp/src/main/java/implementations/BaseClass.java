package implementations;

import interfaces.INaming;

import java.rmi.RemoteException;

public class BaseClass implements INaming {
    private String name;
    @Override
    public String getName() throws RemoteException {
        return name;
    }
    BaseClass(String name) throws RemoteException {
        this.name = name;
    }
}
