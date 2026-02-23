package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITailor extends Remote {
    boolean register(String name, Remote r) throws RemoteException; // służy do rejestrowania namiastek razem z nazwą
    boolean unregister(String name) throws RemoteException; // służy do wyrejestrowywania namiastek
    Remote getRemote(String name) throws RemoteException;
}
