package hu.elte.szakdolgozat.services;

public class InfrastructureException extends Exception {

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
