package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class WebSocketConnectionException extends NotificationException {

    public WebSocketConnectionException(String message) {
        super(message,
                HttpStatus.SERVICE_UNAVAILABLE,
                "WEBSOCKET_CONNECTION_ERROR");
    }

    public WebSocketConnectionException(String message, Throwable cause) {
        super(message, cause,
                HttpStatus.SERVICE_UNAVAILABLE,
                "WEBSOCKET_CONNECTION_ERROR");
    }
}