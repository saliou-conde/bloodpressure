package ch.bloodpressure.handler;

import ch.bloodpressure.exception.*;
import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandling {

    private static final String BLOODPRESSURE_NOT_FOUND_BY_ID = "Bloodpressure does not exist in the database";
    private static final String PATIENT_NOT_FOUND_BY_ID = "Patient does not exist in the database";
    private static final String PATIENT_EMAIL_ALREADY_EXISTS = "Patient email already exists in the database";
    private static final String BP_USER_EMAIL_ALREADY_EXISTS = "BPUser email already exists in the database";
    private static final String BP_USER_NOT_FOUND_BY_USERNAME = "BPUser does not exist in the database";
    private static final String BP_USER_OR_PASSWORD_INVALID = "Username or password is invalid";

    // --- NEUER HANDLER FÜR 401 UNAUTHORIZED ---
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(Exception ex) {
        return generateResponseEntity(ex.getMessage(), UNAUTHORIZED, BP_USER_OR_PASSWORD_INVALID);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException ex) {
        return generateResponseEntity( ex.getMessage(), INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(BPUserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBPUserNotFoundException(BPUserNotFoundException ex) {
        return generateResponseEntity( ex.getMessage(), NOT_FOUND, BP_USER_NOT_FOUND_BY_USERNAME);
    }


    @ExceptionHandler(BPUserAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleBPUserAlreadyExistException(BPUserAlreadyExistException ex) {
        return generateResponseEntity( ex.getMessage(), CONFLICT, BP_USER_EMAIL_ALREADY_EXISTS);
    }

    @ExceptionHandler(InvocationTargetException.class)
    public ResponseEntity<ProblemDetail> handleInvocationTargetException(InvocationTargetException ex) {
        return generateResponseEntity( ex.getMessage(), BAD_REQUEST, BP_USER_OR_PASSWORD_INVALID);
    }

    @ExceptionHandler(BloodpressureNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBloodpressureNotFoundException(BloodpressureNotFoundException ex) {
        return generateResponseEntity( ex.getMessage(), NOT_FOUND, BLOODPRESSURE_NOT_FOUND_BY_ID);
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<ProblemDetail> handleLazyInitializationException(LazyInitializationException ex) {
        return generateResponseEntity( ex.getMessage(), INTERNAL_SERVER_ERROR, "Failed to load lazy data");
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePatientNotFoundException(PatientNotFoundException ex) {
        return generateResponseEntity( ex.getMessage(), NOT_FOUND, PATIENT_NOT_FOUND_BY_ID);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handlePatientEmailAlreadyExistException(EmailAlreadyExistException ex) {
        return generateResponseEntity( ex.getMessage(), BAD_REQUEST, PATIENT_EMAIL_ALREADY_EXISTS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var errors = new HashMap<String, Object>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        String message = "Validation failed for request body. Please check the input values.";
        var status = BAD_REQUEST;
        ProblemDetail problemDetail = generateProblemDetail(message, status, null, errors);
        return new ResponseEntity<>(problemDetail, status);
    }

    private ResponseEntity<ProblemDetail> generateResponseEntity(String message, HttpStatus status, String description) {
        ProblemDetail problemDetail = generateProblemDetail(message, status, description, null);
        return new ResponseEntity<>(problemDetail, status);
    }

    private ProblemDetail generateProblemDetail(String message, HttpStatus status, String title, Map<String, Object> properties) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(Optional.ofNullable(title).orElse(status.getReasonPhrase()));
        problemDetail.setProperties(Optional.ofNullable(properties).orElseGet(HashMap::new));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

}
