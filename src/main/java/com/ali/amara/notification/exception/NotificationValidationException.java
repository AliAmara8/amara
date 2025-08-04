package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class NotificationValidationException extends NotificationException {

    public NotificationValidationException(String field, String message) {
        super("Erreur de validation pour le champ '" + field + "': " + message,
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                field, message);
    }

    public NotificationValidationException(String message) {
        super(message,
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR");
    }
}