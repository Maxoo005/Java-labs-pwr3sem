package org.example.domain.exception;

//plik zablokowany przez inny proces
//sytem odmówił dostępu
//wystąpił inny problem
public class FileLockedOrBusyException extends RuntimeException {
    public FileLockedOrBusyException(String message) {
        super(message);
    }
    public FileLockedOrBusyException(String message, Throwable cause) {
        super(message, cause);
    }
}
