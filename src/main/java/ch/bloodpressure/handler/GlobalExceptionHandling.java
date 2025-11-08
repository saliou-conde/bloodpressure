package ch.bloodpressure.handler;

import ch.bloodpressure.exception.BloodpressureNotFoundException;
import ch.bloodpressure.exception.EmailAlreadyExistException;
import ch.bloodpressure.exception.PatientNotFoundException;
import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException ex) {
    return generateResponseEntity( ex.getMessage(), INTERNAL_SERVER_ERROR, ex.getMessage());
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
