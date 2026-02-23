package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INaming extends Remote {
    String getName() throws RemoteException;
}
