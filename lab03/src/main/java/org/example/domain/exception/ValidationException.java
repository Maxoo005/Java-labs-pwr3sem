package org.example.domain.exception;

//błędy logicze, zamówienie w przeszłośći, pusta nazwa usługiz cena mniej niż 0
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
