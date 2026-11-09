package ch.bloodpressure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BPUserNotFoundException extends RuntimeException {
    public BPUserNotFoundException(String message) {
        super(message);
    }
}
