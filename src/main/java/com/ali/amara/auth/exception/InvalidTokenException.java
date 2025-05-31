package com.ali.amara.auth.exception;

public class InvalidTokenException extends BaseAuthException {

    private static final String ERROR_CODE = "INVALID_TOKEN";

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
