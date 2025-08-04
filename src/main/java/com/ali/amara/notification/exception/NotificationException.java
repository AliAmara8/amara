package com.ali.amara.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotificationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final Object[] args;

    public NotificationException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "NOTIFICATION_ERROR";
        this.args = new Object[0];
    }

    public NotificationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = "NOTIFICATION_ERROR";
        this.args = new Object[0];
    }

    public NotificationException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = new Object[0];
    }

    public NotificationException(String message, HttpStatus httpStatus, String errorCode, Object... args) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = args;
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "NOTIFICATION_ERROR";
        this.args = new Object[0];
    }

    public NotificationException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
}
