package com.ali.amara.notification.exception;

import org.springframework.http.HttpStatus;

public class TemplateNotFoundException extends NotificationException {

    public TemplateNotFoundException(String templateId) {
        super("Template non trouvé avec l'ID: " + templateId,
                HttpStatus.NOT_FOUND,
                "TEMPLATE_NOT_FOUND",
                templateId);
    }

    public TemplateNotFoundException(String templateId, String type) {
        super("Template non trouvé avec l'ID: " + templateId + " et le type: " + type,
                HttpStatus.NOT_FOUND,
                "TEMPLATE_NOT_FOUND",
                templateId, type);
    }
}
