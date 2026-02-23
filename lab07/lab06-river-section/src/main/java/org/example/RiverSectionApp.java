package org.example;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.IEnvironment;
import interfaces.ITailor;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RiverSectionApp {

    public static void main(String[] args) {
        String tailorHost = "localhost";
        int tailorPort = 1099;
        String tailorName = "Tailor";

        String myName = "River1";
        String targetBasinName = "";
        String envName = "Environment1";
        int delay = 0;

        // Simple args parsing
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
        if (args.length > 4)
            targetBasinName = args[4];
        if (args.length > 5)
            envName = args[5];

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== RZEKA URUCHOMIONA (" + myName + ") ===");

        if (delay == 0) {
            System.out.print("Podaj opóźnienie rzeki: ");
            try {
                delay = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
            }
        }

        if (targetBasinName.isEmpty()) {
            System.out.print("Podaj nazwę Zbiornika docelowego (pusty = brak): ");
            targetBasinName = scanner.nextLine().trim();
        }

        try {
            // Logic State
            RiverState state = new RiverState(0, delay);

            //RMI
            RiverSectionImpl impl = new RiverSectionImpl(myName, state);
            IRiverSection stub = (IRiverSection) UnicastRemoteObject.exportObject(impl, 0);

            // Connect to Tailor
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

            // 4. Register Self
            tailor.register(myName, stub);
            System.out.println("[RMI] Registered as: " + myName);

            //Connect to Environment
            if (!envName.isEmpty()) {
                try {
                    IEnvironment env = (IEnvironment) tailor.getRemote(envName);
                    if (env != null) {
                        env.assignRiverSection(stub);
                        System.out.println("[RMI] Assigned to Environment: " + envName);
                    } else {
                        System.out.println("[RMI] Environment not found: " + envName);
                    }
                } catch (Exception e) {
                    System.out.println("[RMI] Failed to assign to Environment: " + e.getMessage());
                }
            }

            // Connect to Downstream Basin
            if (!targetBasinName.isEmpty()) {
                try {
                    IRetensionBasin basin = (IRetensionBasin) tailor.getRemote(targetBasinName);
                    if (basin != null) {
                        state.setDownstreamBasin(basin);
                        System.out.println("[RMI] Output set to Basin: " + targetBasinName);
                    } else {
                        System.out.println("[RMI] Target Basin not found: " + targetBasinName);
                    }
                } catch (Exception e) {
                    System.out.println("[RMI] Failed to connect to Basin: " + e.getMessage());
                }
            }

            System.out.println("\n=== RZEKA PŁYNIE ===");

            // GUI
            RiverSectionGui gui = new RiverSectionGui(state, myName);
            javax.swing.SwingUtilities.invokeLater(() -> gui.setVisible(true));

            // 7. Simulation Loop
            while (true) {
                Thread.sleep(1000);

                int outflow = state.tick();

                // Update GUI
                javax.swing.SwingUtilities.invokeLater(
                        () -> gui.updateValues(state.getUpstreamDischarge(), state.getCurrentRainfall(), outflow));

                IRetensionBasin basin = state.getDownstreamBasin();
                if (outflow > 0) {
                    if (basin != null) {
                        try {
                            basin.setWaterInflow(outflow, stub);
                            System.out.println("[NURT] Płynie " + outflow + " m3/s -> Zbiornik");
                        } catch (Exception e) {
                            System.out.println("[NURT] Błąd wysyłania wody: " + e.getMessage());
                        }
                    } else {
                        System.out.println("[NURT] Płynie " + outflow + " m3/s -> (w pustkę)");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
