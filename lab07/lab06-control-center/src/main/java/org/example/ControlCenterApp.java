package org.example;

import interfaces.IControlCenter;
import interfaces.ITailor;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.SwingUtilities;

public class ControlCenterApp {
    public static void main(String[] args) {
        String tailorHost = "localhost";
        int tailorPort = 1099;
        String tailorName = "Tailor";

        String myName = "ControlCenter1";

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

        try {
            // Logika
            ControlState state = new ControlState();

            // RMI
            ControlCenterImpl impl = new ControlCenterImpl(myName, state);
            IControlCenter stub = (IControlCenter) UnicastRemoteObject.exportObject(impl, 0);

            // conect z Tailor
            Registry registry = null;
            ITailor tailor = null;
            boolean connected = false;
            for (int i = 0; i < 10; i++) {
                try {
                    registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
                    tailor = (ITailor) registry.lookup(tailorName);
                    connected = true;
                    break;
                } catch (Exception e) {
                    System.out.println("[INFO] Waiting for Tailor (" + (i + 1) + "/10)...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (!connected) {
                System.err.println("[ERROR] Could not connect to Tailor at " + tailorHost + ":" + tailorPort);
                System.exit(1);
            }

            // Rejestracjas
            tailor.register(myName, stub);
            System.out.println("[RMI] Registered as: " + myName);

            // GUI
            final String finalMyName = myName;
            System.out.println("=== CENTRALA STEROWANIA URUCHOMIONA ===");
            SwingUtilities.invokeLater(() -> {
                ControlCenterGui gui = new ControlCenterGui(state);
                gui.setTitle("Centrala Sterowania - " + finalMyName);
                gui.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
