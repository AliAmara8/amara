package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends NotificationException {

    public RateLimitException(String userId, int limit, String period) {
        super("Limite de taux dépassée pour l'utilisateur " + userId +
                        ": " + limit + " notifications par " + period,
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED",
                userId, limit, period);
    }

    public RateLimitException(String message) {
        super(message,
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED");
    }
}