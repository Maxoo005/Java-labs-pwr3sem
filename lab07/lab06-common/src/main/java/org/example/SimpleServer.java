package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/// Wyłączony z użytku


public class SimpleServer {
    private final int port;
    private final MessageHandler handler;
    private boolean isRunning = true;

    public SimpleServer(int port, MessageHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() {
        // Server uruchamiany w nowym wątku, by nie blokował pętli symulacji
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("[SERVER] Nasłuchuję na porcie: " + port);

                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    // Każdy klient w osobnym wątku, non-blocking dla reszty
                    new Thread(() -> handleClient(clientSocket)).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        //Try-with zamyka socket automatycznie
        try (
                clientSocket;
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            // Sprawdzamy co klient przysłał
            if ((inputLine = in.readLine()) != null) {
                // Logika biznesowa - handler
                String response = handler.handle(inputLine);

                // Jeśli handler zwrócił odpowiedź, wysyłamy ją
                if (response != null) {
                    out.println(response);
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Błąd obsługi klienta: " + e.getMessage());
        }
    }

    public void stop() {
        this.isRunning = false;
    }
}
