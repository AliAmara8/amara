package com.ali.amara.auth.exception;

public abstract class BaseAuthException extends RuntimeException {

    protected BaseAuthException(String message) {
        super(message);
    }

    protected BaseAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getErrorCode();
}