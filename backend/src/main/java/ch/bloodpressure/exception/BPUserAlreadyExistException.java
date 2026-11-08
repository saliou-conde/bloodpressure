package ch.bloodpressure.exception;

public class BPUserAlreadyExistException extends RuntimeException {
    public BPUserAlreadyExistException(String message) {
        super(message);
    }
}
