package com.ali.amara.notification.exception;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ExceptionUtils {

    /**
     * Enregistre une exception avec un message personnalisé
     */
    public static void logException(String operation, Exception ex) {
        log.error("Erreur lors de l'opération '{}': {}", operation, ex.getMessage(), ex);
    }

    /**
     * Enregistre une exception avec des paramètres
     */
    public static void logException(String operation, Exception ex, Object... params) {
        log.error("Erreur lors de l'opération '{}' avec paramètres {}: {}",
                operation, params, ex.getMessage(), ex);
    }

    /**
     * Vérifie si une exception est récupérable
     */
    public static boolean isRecoverableException(Exception ex) {
        return ex instanceof NotificationSendException ||
                ex instanceof ProviderException ||
                ex instanceof WebSocketConnectionException;
    }

    /**
     * Extrait le message d'erreur racine
     */
    public static String getRootCauseMessage(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }

    /**
     * Détermine le niveau de criticité d'une exception
     */
    public static String getExceptionSeverity(Exception ex) {
        if (ex instanceof NotificationValidationException ||
                ex instanceof NotificationNotFoundException) {
            return "LOW";
        } else if (ex instanceof NotificationSendException ||
                ex instanceof ProviderException) {
            return "MEDIUM";
        } else if (ex instanceof WebSocketAuthenticationException) {
            return "HIGH";
        }
        return "UNKNOWN";
    }
}