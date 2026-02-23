package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IEnvironment extends INaming {
    void assignRiverSection(IRiverSection irs) throws RemoteException;
}
