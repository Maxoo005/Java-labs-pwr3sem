package implementations;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
  Informację na temat tego na jakim hoście i porcie rzeczywiście
  działa namiastka chyba najłatwiej uzyskać parsując wynik metody
  toString() tej namiastki. Metoda ta zwróci ciąg znaków podobny
  do poniższego:
           Proxy[IControlCenter,RemoteObjectInvocationHandler[UnicastRef [liveRef: [endpoint:[192.168.1.153:3000](remote),objID:[-50fb9f25:1945c684c6d:-7fff, 7487353482432237380]]]]]
  Port wyciągnąć można z niego korzystając z regexp
         Pattern pattern = Pattern.compile(".*endpoint:\\[(.*)\\]\\(remote.*");
         Matcher matcher = pattern.matcher(r.toString());
         if (matcher.find())
         {
         System.out.println(matcher.group(1));
         }
 */
public class ControlCenter extends UnicastRemoteObject implements IControlCenter {
    private Registry rmiRegistry;
    private final String ccname;
    private Map<String,IRetensionBasin> retensionBasinMap = new HashMap<>();

    protected ControlCenter(String ccname, Registry rmiRegistry) throws RemoteException {
        this.ccname = ccname;
        this.rmiRegistry = rmiRegistry;
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb) {
         try {
             String name = irb.getName();
             retensionBasinMap.put(name,irb);
            IO.println("assigned retension basin "+name);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String rmiRegistryHost = "localhost";
        int rmiRegistryPort = 1099;
        Registry rmiRegistry = null;

        try {
            rmiRegistry = LocateRegistry.getRegistry(rmiRegistryHost,rmiRegistryPort);
            ControlCenter cc = new ControlCenter("ControlCenter", rmiRegistry);
            ((ITailor) rmiRegistry.lookup("Tailor")).register("ControlCenter",cc);

            // jeśli ControlCenter nie dziedziczyłoby po UnicastRemoteObject,
            // wtedy należałoby "wyeksportować obiekt", by utworzyła się namiastka
            // oraz obiekt zaczął nasłuchiwać na wskazanym porcie
            // (przekazując 0 mówi się, że port ten ma zostać utworzony
            //  przez fabrykę gniazdek o wybranym przez nią numerze
            // IControlCenter icc = (IControlCenter) UnicastRemoteObject.exportObject(cc,0);

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getName() throws RemoteException {
        return this.ccname;
    }
}
