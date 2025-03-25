package com.qpa.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Create a map to store the error messages
        Map<String, String> errors = new HashMap<>();

        // Extract all the field errors
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            // Only include the field name and error message
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        // Return a simplified error response
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handle other specific exceptions if needed
    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(InvalidEntityException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}

/*

put by amit 
 * package com.qpa.exception;
 * 
 * import java.util.HashMap;
 * import java.util.Map;
 * 
 * import org.springframework.http.HttpStatus;
 * import org.springframework.http.ResponseEntity;
 * import org.springframework.web.bind.MethodArgumentNotValidException;
 * import org.springframework.web.bind.annotation.ExceptionHandler;
 * import org.springframework.web.bind.annotation.RestControllerAdvice;
 * import org.springframework.web.multipart.MaxUploadSizeExceededException;
 * 
 * import com.qpa.dto.ResponseDTO;
 * 
 * @RestControllerAdvice
 * public class GlobalExceptionHandler {
 * // Handle validation errors for input fields
 * 
 * @ExceptionHandler(MethodArgumentNotValidException.class)
 * public ResponseEntity<ResponseDTO<Map<String, String>>>
 * handleValidationExceptions(
 * MethodArgumentNotValidException ex) {
 * Map<String, String> errors = new HashMap<>();
 * ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
 * errors.put(fieldError.getField(), fieldError.getDefaultMessage());
 * });
 * 
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
 * .body(new ResponseDTO<>("Validation failed", 400, false, errors));
 * }
 * 
 * // Handle cases where a requested entity (User/Vehicle) is not found
 * 
 * @ExceptionHandler(InvalidEntityException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleInvalidEntityException(InvalidEntityException ex) {
 * return ResponseEntity.status(HttpStatus.NOT_FOUND)
 * .body(new ResponseDTO<>(ex.getMessage(), 404, false));
 * }
 * 
 * // Handle file upload size exceeding limit
 * 
 * @ExceptionHandler(MaxUploadSizeExceededException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleMaxSizeException(MaxUploadSizeExceededException ex) {
 * return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
 * .body(new ResponseDTO<>
 * ("File size exceeds the maximum allowed limit. Please upload a smaller file."
 * ,
 * 413, false));
 * }
 * 
 * // Handle SQL exceptions (like duplicate entry errors)
 * 
 * @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.
 * class)
 * public ResponseEntity<ResponseDTO<Void>> handleSQLIntegrityViolation(
 * org.springframework.dao.DataIntegrityViolationException ex) {
 * return ResponseEntity.status(HttpStatus.CONFLICT)
 * .body(new ResponseDTO<>("Duplicate entry or constraint violation", 409,
 * false));
 * }
 * 
 * // Handle all other unhandled exceptions
 * 
 * @ExceptionHandler(Exception.class)
 * public ResponseEntity<ResponseDTO<Void>> handleGeneralException(Exception ex)
 * {
 * System.out.println(ex.getMessage());
 * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
 * .body(new ResponseDTO<>("An unexpected error occurred", 500, false));
 * }
 * 
 * @ExceptionHandler(InvalidVehicleTypeException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleInvalidVehicleType(InvalidVehicleTypeException ex) {
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
 * .body(new ResponseDTO<>(ex.getMessage(), 400, false));
 * }
 * 
 * @ExceptionHandler(UnauthorizedAccessException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleUnauthorizedAccess(UnauthorizedAccessException ex) {
 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 * .body(new ResponseDTO<>(ex.getMessage(), 401, false));
 * }
 * 
 * @ExceptionHandler(InvalidCredentialsException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleInvalidCredentials(InvalidCredentialsException ex) {
 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 * .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(),
 * false));
 * }
 * 
 * @ExceptionHandler(ResourceNotFoundException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleResourceNotFoundException(ResourceNotFoundException ex) {
 * 
 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 * .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(),
 * false));
 * }
 * }
 */