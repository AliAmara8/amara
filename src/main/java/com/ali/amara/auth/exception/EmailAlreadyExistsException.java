package com.ali.amara.auth.exception;

public class EmailAlreadyExistsException extends BaseAuthException {

    private static final String ERROR_CODE = "EMAIL_ALREADY_EXISTS";

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}