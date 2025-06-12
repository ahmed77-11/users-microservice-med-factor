package com.medfactor.factorusers.exception;

import com.medfactor.factorusers.dtos.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle authentication-related exceptions
    @ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
    public ResponseEntity<?> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid email or password"));
    }

    // Handle DataIntegrityViolationException for duplicate entries
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable rootCause = getRootCause(ex);
        if (rootCause != null && rootCause.getMessage() != null) {
            String lowerMsg = rootCause.getMessage().toLowerCase();
            System.out.println("Handling DataIntegrityViolationException: " + lowerMsg);
            if (lowerMsg.contains("duplicate")
                    || lowerMsg.contains("dupliquée")
                    || lowerMsg.contains("contrainte unique")) {
                // Return a 409 Conflict status with a custom French message
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cet utilisateur existe déjà.");
            }
        }
        // Fallback for any other cases
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur interne est survenue.");
    }

    // Helper method to find the deepest cause of an exception
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause == null || cause == throwable) {
            return throwable;
        }
        return getRootCause(cause);
    }
}
