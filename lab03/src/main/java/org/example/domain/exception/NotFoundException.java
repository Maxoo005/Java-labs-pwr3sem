package org.example.domain.exception;

/* dzięki temu
 -opracja sie zatrzyma
 /informaja dla uzytkownilka
 -odróznienie braków danych od innych błędów
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
