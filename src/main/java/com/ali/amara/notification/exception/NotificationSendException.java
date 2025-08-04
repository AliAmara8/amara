package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class NotificationSendException extends NotificationException {

    public NotificationSendException(String message) {
        super(message,
                HttpStatus.SERVICE_UNAVAILABLE,
                "SEND_ERROR");
    }

    public NotificationSendException(String message, Throwable cause) {
        super(message, cause,
                HttpStatus.SERVICE_UNAVAILABLE,
                "SEND_ERROR");
    }

    public NotificationSendException(String channel, String reason) {
        super("Erreur d'envoi sur le canal " + channel + ": " + reason,
                HttpStatus.SERVICE_UNAVAILABLE,
                "SEND_ERROR",
                channel, reason);
    }

    public NotificationSendException(String channel, String reason, Throwable cause) {
        super("Erreur d'envoi sur le canal " + channel + ": " + reason,
                cause,
                HttpStatus.SERVICE_UNAVAILABLE,
                "SEND_ERROR");
    }
}
