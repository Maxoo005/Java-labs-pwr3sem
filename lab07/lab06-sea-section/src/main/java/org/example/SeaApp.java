package org.example;

import interfaces.IRetensionBasin;
import interfaces.ITailor;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class SeaApp {

    public static void main(String[] args) {
        String tailorHost = "localhost";
        int tailorPort = 1099;
        String tailorName = "Tailor";

        String myName = "Sea1";

        if (args.length > 0)
            tailorHost = args[0];
        if (args.length > 1)
            try {
                tailorPort = Integer.parseInt(args[1]);
            } catch (Exception e) {
            }
        if (args.length > 2)
            tailorName = args[2];
        if (args.length > 3)
            myName = args[3];

        System.out.println("=== MORZE URUCHOMIONE (" + myName + ") ===");

        try {
            //Implementacja
            SeaImpl impl = new SeaImpl(myName);
            IRetensionBasin stub = (IRetensionBasin) UnicastRemoteObject.exportObject(impl, 0);

            //Connet z Tailor
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor tailor = (ITailor) registry.lookup(tailorName);

            //Zarejestrowanie
            tailor.register(myName, stub);
            System.out.println("[RMI] Registered as: " + myName);

            System.out.println("Oczekiwanie na wodę...");

            // Keep alive
            while (true) {
                Thread.sleep(10000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
