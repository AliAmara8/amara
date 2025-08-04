package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class ProviderException extends NotificationException {

    public ProviderException(String provider, String message) {
        super("Erreur du provider " + provider + ": " + message,
                HttpStatus.SERVICE_UNAVAILABLE,
                "PROVIDER_ERROR",
                provider, message);
    }

    public ProviderException(String provider, String message, Throwable cause) {
        super("Erreur du provider " + provider + ": " + message, cause,
                HttpStatus.SERVICE_UNAVAILABLE,
                "PROVIDER_ERROR");
    }
}