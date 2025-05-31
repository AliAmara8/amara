package com.ali.amara.auth.exception;

import java.time.Instant;

public class TooManyAttemptsException extends BaseAuthException {

    private static final String ERROR_CODE = "TOO_MANY_ATTEMPTS";
    private final Instant retryAfter;

    public TooManyAttemptsException(String message) {
        super(message);
        this.retryAfter = null;
    }

    public TooManyAttemptsException(String message, Instant retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    public Instant getRetryAfter() {
        return retryAfter;
    }
}