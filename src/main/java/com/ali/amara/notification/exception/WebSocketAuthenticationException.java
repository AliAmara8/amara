package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class WebSocketAuthenticationException extends NotificationException {

    public WebSocketAuthenticationException(String message) {
        super(message,
                HttpStatus.UNAUTHORIZED,
                "WEBSOCKET_AUTH_ERROR");
    }

    public WebSocketAuthenticationException(String message, Throwable cause) {
        super(message, cause,
                HttpStatus.UNAUTHORIZED,
                "WEBSOCKET_AUTH_ERROR");
    }
}