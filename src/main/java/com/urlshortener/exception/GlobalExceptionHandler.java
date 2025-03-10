package com.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling custom exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UrlNotFoundException and returns a detailed error response.
     *
     * @param ex the UrlNotFoundException
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleUrlNotFoundException(UrlNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles UrlExpiredException and returns a detailed error response.
     *
     * @param ex the UrlExpiredException
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<String> handleUrlExpiredException(UrlExpiredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.GONE);
    }

    /**
     * Handles MethodArgumentNotValidException and returns a detailed error response.
     *
     * @param ex the MethodArgumentNotValidException
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
