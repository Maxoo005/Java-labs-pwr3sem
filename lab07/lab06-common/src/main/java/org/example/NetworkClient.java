package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkClient {

    /**
     * Wysyła wiadomość do wskazanego hosta i portu.
     * Zwraca odpowiedź serwera lub null, jeśli serwer nic nie odpowiedział/błąd.
     */
    public static String sendRequest(String host, int port, String message) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("[CLIENT] Nie udało się wysłać do " + host + ":" + port + " -> " + e.getMessage());
            return null;
        }
    }
}
