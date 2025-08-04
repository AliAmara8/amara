package com.ali.amara.exception;

import com.ali.amara.auth.exception.EmailAlreadyExistsException;
import com.ali.amara.auth.exception.InvalidTokenException;
import com.ali.amara.exception.dto.ErrorResponse;
import com.ali.amara.notification.exception.NotificationException;
import jakarta.mail.AuthenticationFailedException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UnifiedGlobalExceptionHandler {

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationException(
            NotificationException ex, WebRequest request) {
        log.error("Erreur de notification: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                request.getDescription(false).replace("uri=", ""),
                ex.getHttpStatus().value()
        );
        errorResponse.setArgs(ex.getArgs());

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse(
                "Erreur de validation",
                "VALIDATION_ERROR",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.BAD_REQUEST.value()
        );
        errorResponse.setDetails(errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                "Erreur de validation des contraintes",
                "CONSTRAINT_VIOLATION",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.BAD_REQUEST.value()
        );
        errorResponse.setDetails(errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({
            AuthenticationFailedException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthException(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "AUTHENTICATION_ERROR",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            InvalidTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "BUSINESS_ERROR",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Accès refusé",
                "ACCESS_DENIED",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.FORBIDDEN.value()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception ex, WebRequest request) {
        log.error("Erreur inattendue", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "Une erreur inattendue s'est produite",
                "INTERNAL_ERROR",
                request.getDescription(false).replace("uri=", ""),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
