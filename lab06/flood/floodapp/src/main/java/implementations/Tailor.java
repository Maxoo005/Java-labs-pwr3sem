package implementations;

import interfaces.IControlCenter;
import interfaces.ITailor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 Klasa Tailor służy do utworzenia rejestru RMI programowo.

 Rejestr RMI można byłoby spróbować uruchomić  z linii komend
 jako osobny program, ale zgodnie ze sztuką wymagałoby to
 przekazania konfiguracji codebase
 rmiregistry -J-Djava.rmi.server.codebase=<sciezkaklas> -J-Djava.security.policy=<plik-policy> 2000
 np.  rmiregistry -J-Djava.rmi.server.codebase=file://E:\Dydaktyka\JęzykiProgramowania\2024
 \projektyIntelliJ\floodlib-1.0-SNAPSHOT.jar -J-DJava.security.policy=.\server.policy  2000

 Wtedy też może pojawić się problem z konfiguracją menadżera bezpieczeństwa,
 gdy będzie się chciało z tego rejestru skorzystać (w innych aplikacjach):

 if (System.getSecurityManager() == null){
 System.setSecurityManager(new RMISecurityManager());
 }
 Registry r = LocateRegistry.getRegistry(port);
 */


public class Tailor implements ITailor {
    Map<String,Remote> map = new HashMap<>();

    @Override
    public boolean register(String name, Remote r) throws RemoteException {
        map.put(name,r);
        return true;
    }


    @Override
    public boolean unregister(String name) throws RemoteException {
        map.remove(name);
        return true;
    }

    @Override
    public Remote getRemote(String name) throws RemoteException {
        return map.get(name);
    }

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);

            Tailor tailor = new Tailor();

            ITailor it = (ITailor) UnicastRemoteObject.exportObject(tailor,0);
            Registry r = LocateRegistry.createRegistry(port);
            r.rebind("Tailor", it);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Usage: java -jar Tailor.jar <port>");
            System.exit(1);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
// -Djava.rmi.server.codebase=file://E:\Dydaktyka\JęzykiProgramowania\2024\projektyIntelliJ\sewagelib-1.0-SNAPSHOT.jar -Djava.security.policy=E:\Dydaktyka\JęzykiProgramowania\2024\projektyIntelliJ\client.policy
