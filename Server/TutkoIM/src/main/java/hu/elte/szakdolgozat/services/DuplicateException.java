package hu.elte.szakdolgozat.services;

public class DuplicateException extends Exception {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
