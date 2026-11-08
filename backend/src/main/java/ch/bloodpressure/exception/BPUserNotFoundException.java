package ch.bloodpressure.exception;

public class BPUserNotFoundException extends RuntimeException {
    public BPUserNotFoundException(String message) {
        super(message);
    }
}
