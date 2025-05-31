package com.ali.amara.auth.exception;

import java.util.List;

public class WeakPasswordException extends BaseAuthException {

    private static final String ERROR_CODE = "WEAK_PASSWORD";
    private final List<String> violations;

    public WeakPasswordException(String message) {
        super(message);
        this.violations = List.of();
    }

    public WeakPasswordException(String message, List<String> violations) {
        super(message);
        this.violations = violations != null ? violations : List.of();
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    public List<String> getViolations() {
        return violations;
    }
}