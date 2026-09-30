package com.example.lab1.presentation.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final URI VALIDATION_ERROR = URI.create("https://cosmo-cats.market/errors/validation");
    private static final URI NOT_FOUND_ERROR = URI.create("https://cosmo-cats.market/errors/not-found");
    private static final URI INTERNAL_ERROR = URI.create("https://cosmo-cats.market/errors/internal");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String detail = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> "Field '%s' %s."
                        .formatted(
                                error.getField(),
                                error.getDefaultMessage()
                        ))
                .collect(Collectors.joining(" "));

        ErrorResponse response = new ErrorResponse(
                VALIDATION_ERROR,
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                detail,
                URI.create(request.getRequestURI())
        );

        return ResponseEntity .status(HttpStatus.BAD_REQUEST) .body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException exception,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                NOT_FOUND_ERROR,
                "Product not found",
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                URI.create(request.getRequestURI())
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                INTERNAL_ERROR,
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                URI.create(request.getRequestURI())
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
