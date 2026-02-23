package org.example;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RetensionBasinApp {

    public static void main(String[] args) {
        String tailorHost = "localhost";
        int tailorPort = 1099;
        String tailorName = "Tailor";

        String myName = "Basin1";
        String targetRiverName = "";

        // Default Volume
        int volume = 100000;
        int startVolume = 0;

        // Args parsing
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
            targetRiverName = args[4];

        /// TUTAJ

        try {
            // Init State
            BasinState state = new BasinState(volume, startVolume);

            // Export RMI
            RetentionBasinImpl impl = new RetentionBasinImpl(myName, state);
            IRetensionBasin stub = (IRetensionBasin) UnicastRemoteObject.exportObject(impl, 0);

            // Connect to Tailor
            Registry registry = null;
            ITailor tailor = null;
            boolean connected = false;
            for (int i = 0; i < 10; i++) {
                try {

                    /// znajdowanie Tailor

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

            // Register Self do rzeki
            tailor.register(myName, stub);
            System.out.println("[RMI] Registered start basin: " + myName);

            // Connect to Downstream River
            if (!targetRiverName.isEmpty()) {
                try {
                    IRiverSection river = (IRiverSection) tailor.getRemote(targetRiverName);
                    if (river != null) {
                        state.setRiverOut(river);
                        System.out.println("[RMI] Output set to River: " + targetRiverName);
                    } else {
                        System.out.println("[RMI] Target River not found: " + targetRiverName);
                    }
                } catch (Exception e) {
                    System.out.println("[RMI] Failed to lookup river: " + e.getMessage());
                }
            }

            /// Connect do centarali
            if (args.length > 5) {
                String ccName = args[5];
                if (!ccName.isEmpty()) {
                    try {
                        interfaces.IControlCenter cc = (interfaces.IControlCenter) tailor.getRemote(ccName);
                        if (cc != null) {
                            cc.assignRetensionBasin(stub);
                            System.out.println("[RMI] Assigned to ControlCenter: " + ccName);
                        } else {
                            System.out.println("[RMI] ControlCenter not found: " + ccName);
                        }
                    } catch (Exception e) {
                        System.out.println("[RMI] Failed to assign to ControlCenter: " + e.getMessage());
                    }
                }
            }

            System.out.println("\n=== ZBIORNIK PRACUJE (" + myName + ") ===");

            // GUI
            final String title = myName;
            final interfaces.ITailor finalTailor = tailor;
            javax.swing.SwingUtilities.invokeLater(() -> {
                RetentionBasinGui gui = new RetentionBasinGui(state, title, finalTailor);
                gui.setVisible(true);
            });

            // Loop
            while (true) {
                Thread.sleep(1000);
                state.tick();

                int discharge = state.getRealDischarge();
                interfaces.IRiverSection river = state.getRiverOut();

                if (river != null) {
                    try {
                        river.setRealDischarge(discharge);
                    } catch (Exception e) {
                        System.out.println("[ZBIORNIK] Błąd zrzutu wody: " + e.getMessage());
                    }
                }

                if (discharge > 0) {
                    if (river != null) {
                        System.out.println("[ZBIORNIK] Zrzut " + discharge + " m3/s -> Rzeka");
                    } else {
                        System.out.println("[ZBIORNIK] Zrzut " + discharge + " m3/s -> (w pole)");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
