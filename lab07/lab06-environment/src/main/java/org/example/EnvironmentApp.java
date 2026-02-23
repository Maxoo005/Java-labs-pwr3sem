package org.example;

import javax.swing.SwingUtilities;

public class EnvironmentApp {
    public static void main(String[] args) {
        String tailorHost = "localhost";
        int tailorPort = 1099;
        String tailorName = "Tailor";
        String myName = "Environment1";

        if (args.length > 0)
            tailorHost = args[0];
        if (args.length > 1) {
            try {
                tailorPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
            }
        }
        if (args.length > 2)
            tailorName = args[2];
        if (args.length > 3)
            myName = args[3];

        try {
            // 1. Setup State and GUI
            EnvironmentState state = new EnvironmentState();
            EnvironmentGui gui = new EnvironmentGui(state);

            // 2. Create and Export Implementation
            EnvironmentImpl envImpl = new EnvironmentImpl(myName, state);
            interfaces.IEnvironment stub = (interfaces.IEnvironment) java.rmi.server.UnicastRemoteObject
                    .exportObject(envImpl, 0);

            // 3. Get Tailor Registry
            java.rmi.registry.Registry registry = null;
            interfaces.ITailor tailor = null;
            boolean connected = false;
            for (int i = 0; i < 10; i++) {
                try {
                    registry = java.rmi.registry.LocateRegistry.getRegistry(tailorHost, tailorPort);
                    tailor = (interfaces.ITailor) registry.lookup(tailorName);
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

            // 4. Register
            tailor.register(myName, stub);
            System.out.println("Environment registered as: " + myName);

            gui.setTitle("Symulator Pogody - " + myName + " [Connected to Tailor]");

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Critical Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
