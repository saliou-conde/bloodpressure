package ch.bloodpressure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BPUserAlreadyExistException extends RuntimeException {
    public BPUserAlreadyExistException(String message) {
        super(message);
    }
}
