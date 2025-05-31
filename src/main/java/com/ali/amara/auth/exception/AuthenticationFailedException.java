package com.ali.amara.auth.exception;

public class AuthenticationFailedException extends BaseAuthException {

    private static final String ERROR_CODE = "AUTHENTICATION_FAILED";

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
