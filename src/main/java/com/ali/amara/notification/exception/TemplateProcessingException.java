package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class TemplateProcessingException extends NotificationException {

    public TemplateProcessingException(String templateId, String message) {
        super("Erreur de traitement du template " + templateId + ": " + message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "TEMPLATE_PROCESSING_ERROR",
                templateId, message);
    }

    public TemplateProcessingException(String templateId, String message, Throwable cause) {
        super("Erreur de traitement du template " + templateId + ": " + message, cause,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "TEMPLATE_PROCESSING_ERROR");
    }
}