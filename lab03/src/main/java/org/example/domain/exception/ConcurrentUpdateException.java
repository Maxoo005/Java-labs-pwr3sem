package org.example.domain.exception;

/*
 *Rzucany, gdy dwa różne procesy lub użytkownicy próbują jednocześnie
 * zaktualizować ten sam oboirkt,
 * a wersja danych w pamięci nie jest już zgodna z wersją w bazie.
 */

public class ConcurrentUpdateException extends RuntimeException {
    public ConcurrentUpdateException(String message) {
        super(message);
    }
}
