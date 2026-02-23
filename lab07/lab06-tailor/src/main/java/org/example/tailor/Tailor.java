package org.example.tailor;

import interfaces.ITailor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Tailor implements ITailor {
    Map<String, Remote> map = new HashMap<>();

    @Override
    public boolean register(String name, Remote r) throws RemoteException {
        System.out.println("Registering: " + name);
        map.put(name, r);
        return true;
    }

    @Override
    public boolean unregister(String name) throws RemoteException {
        System.out.println("Unregistering: " + name);
        map.remove(name);
        return true;
    }

    @Override
    public Remote getRemote(String name) throws RemoteException {
        System.out.println("Lookup: " + name);
        return map.get(name);
    }

    public static void main(String[] args) {
        try {
            int port = 1099;
            String name = "Tailor";
            
            if (args.length > 0) {
                try {
                     port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {

                }
            }
            if (args.length > 1) {
                name = args[1];
            }

            System.out.println("Starting Tailor on port " + port + " with name " + name);

            Tailor tailor = new Tailor();
            ITailor stub = (ITailor) UnicastRemoteObject.exportObject(tailor, 0);

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, stub);

            System.out.println("Tailor ready.");

            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }

        } catch (Exception e) {
            System.err.println("Tailor exception:");
            e.printStackTrace();
        }
    }
}
