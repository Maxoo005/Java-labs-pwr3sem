package implementations;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RetensionBasin extends BaseClass implements IRetensionBasin {
    private Registry rmiRegistry;

    protected RetensionBasin(String rbname) throws RemoteException {
        super(rbname);
    }


    public static void main(String[] args) {
        String rmiRegistryHost = "localhost";
        int rmiRegistryPort = 1099;
        Registry rmiRegistry = null;

        try {
            rmiRegistry = LocateRegistry.getRegistry(rmiRegistryHost,rmiRegistryPort);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            RetensionBasin rb = new RetensionBasin("RetensionBasin");
            IRetensionBasin irb = (IRetensionBasin) UnicastRemoteObject.exportObject(rb,0);

            try {
                ITailor it = (ITailor)rmiRegistry.lookup("Tailor");
                IControlCenter ic = (IControlCenter)it.getRemote("ControlCenter");
                ic.assignRetensionBasin(irb);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getWaterDischarge() throws RemoteException {
        return 0;
    }

    @Override
    public long getFillingPercentage() throws RemoteException {
        return 0;
    }

    @Override
    public void setWaterDischarge(int waterDischarge) throws RemoteException {

    }

    @Override
    public void setWaterInflow(int waterInflow, IRiverSection irs) throws RemoteException {

    }

    @Override
    public void assignRiverSection(IRiverSection irs) throws RemoteException {

    }
}