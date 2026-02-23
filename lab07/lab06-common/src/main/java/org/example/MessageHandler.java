package org.example;

public interface MessageHandler {
    // Przetwarzanie wiadomości, moduł common nie wie, czy jest Zbiornikiem, czy Rzeką
    String handle(String message);
}
