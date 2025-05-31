package com.ali.amara.auth.exception;

public class AccountDisabledException extends BaseAuthException {

    private static final String ERROR_CODE = "ACCOUNT_DISABLED";

    public AccountDisabledException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}