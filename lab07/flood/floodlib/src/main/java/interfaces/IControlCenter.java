package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IControlCenter extends INaming {
    void assignRetensionBasin(IRetensionBasin irb) throws RemoteException; // ustawia namiastkę zbiornika retencyjnego
}
