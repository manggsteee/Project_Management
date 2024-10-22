package project.management.exception;

public class DeniedUserException extends RuntimeException {
    public DeniedUserException(String message) {
        super(message);
    }
}
