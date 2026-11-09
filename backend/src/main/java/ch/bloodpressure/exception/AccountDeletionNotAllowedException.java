package ch.bloodpressure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountDeletionNotAllowedException extends RuntimeException {
    public AccountDeletionNotAllowedException(String message) {
        super(message);
    }
}
