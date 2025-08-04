package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class PreferenceException extends NotificationException {

    public PreferenceException(String userId, String message) {
        super("Erreur de préférence pour l'utilisateur " + userId + ": " + message,
                HttpStatus.BAD_REQUEST,
                "PREFERENCE_ERROR",
                userId, message);
    }

    public PreferenceException(String message) {
        super(message,
                HttpStatus.BAD_REQUEST,
                "PREFERENCE_ERROR");
    }
}