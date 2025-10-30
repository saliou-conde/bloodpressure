package ch.bloodpressure.exception;

public class BloodpressureNotFoundException extends RuntimeException {
    public BloodpressureNotFoundException(String message) {
        super(message);
    }
}
