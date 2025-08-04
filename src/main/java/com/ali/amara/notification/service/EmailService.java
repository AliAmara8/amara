package com.ali.amara.notification.service;

import com.ali.amara.auth.service.JwtService;
import com.ali.amara.notification.entity.Notification;
import com.ali.amara.notification.exception.NotificationSendException;
import com.ali.amara.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtService jwtService;

    @Value("${app.mail.from:noreply@amara.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:Amara}")
    private String fromName;

    /**
     * Envoie un email de notification
     */
    public void sendNotificationEmail(Notification notification) {
        try {
            if (notification.getRecipient() == null || notification.getRecipient().getEmail() == null) {
                log.warn("Cannot send email notification - recipient email is null for notification: {}",
                        notification.getId());
                return;
            }

            String recipientEmail = notification.getRecipient().getEmail();
            String subject = generateSubject(notification);
            String content = generateEmailContent(notification);

            sendHtmlEmail(recipientEmail, subject, content);

            log.info("Email notification sent successfully to: {} for notification: {}",
                    recipientEmail, notification.getId());

        } catch (Exception e) {
            log.error("Failed to send email notification: {}", notification.getId(), e);
            throw new NotificationSendException("EMAIL", "Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Envoie un email simple
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            throw new NotificationSendException("EMAIL", "Failed to send simple email: " + e.getMessage(), e);
        }
    }

    /**
     * Envoie un email HTML
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new NotificationSendException("EMAIL", "Failed to send HTML email: " + e.getMessage(), e);
        }
    }

    /**
     * Génère le sujet de l'email
     */
    private String generateSubject(Notification notification) {
        return switch (notification.getType()) {
            case RESERVATION_REQUEST -> "Nouvelle demande de réservation";
            case RESERVATION_CONFIRMED -> "Réservation confirmée";
            case RESERVATION_REJECTED -> "Réservation rejetée";
            case RESERVATION_CANCELLED -> "Réservation annulée";
            case RESERVATION_OVERDUE -> "Réservation en retard";
            case EQUIPMENT_MAINTENANCE_URGENT -> "🚨 Maintenance urgente requise";
            case EQUIPMENT_MAINTENANCE_HIGH -> "⚠️ Maintenance prioritaire";
            case EQUIPMENT_MAINTENANCE -> "Maintenance requise";
            default -> "Notification - " + notification.getType().getDefaultMessage();
        };
    }

    /**
     * Génère le contenu HTML de l'email avec templates statiques
     */
    private String generateEmailContent(Notification notification) {
        String recipientName = notification.getRecipient().getFullName();
        String actorName = notification.getActor() != null ? notification.getActor().getFullName() : "";
        String message = notification.getMessage();
        String link = notification.getLink();

        return switch (notification.getType()) {
            case RESERVATION_REQUEST -> buildReservationRequestEmail(recipientName, actorName, message, link);
            case RESERVATION_CONFIRMED -> buildReservationConfirmedEmail(recipientName, actorName, message, link);
            case RESERVATION_REJECTED -> buildReservationRejectedEmail(recipientName, actorName, message, link);
            case RESERVATION_CANCELLED -> buildReservationCancelledEmail(recipientName, actorName, message, link);
            case RESERVATION_OVERDUE -> buildReservationOverdueEmail(recipientName, message, link);
            case EQUIPMENT_MAINTENANCE_URGENT -> buildMaintenanceUrgentEmail(recipientName, message, link);
            case EQUIPMENT_MAINTENANCE_HIGH -> buildMaintenanceHighEmail(recipientName, message, link);
            case EQUIPMENT_MAINTENANCE -> buildMaintenanceEmail(recipientName, message, link);
            default -> buildDefaultEmail(recipientName, message, link);
        };
    }

    private String buildReservationRequestEmail(String recipientName, String actorName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Nouvelle demande de réservation</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>%s a fait une demande de réservation :</p>
                        <p><strong>%s</strong></p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                actorName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir la demande</a></p>" : ""
        );
    }

    private String buildReservationConfirmedEmail(String recipientName, String actorName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Réservation confirmée</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Votre réservation a été confirmée :</p>
                        <p><strong>%s</strong></p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir la réservation</a></p>" : ""
        );
    }

    private String buildReservationRejectedEmail(String recipientName, String actorName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #dc3545; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>❌ Réservation rejetée</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Votre demande de réservation a été rejetée :</p>
                        <p><strong>%s</strong></p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir les détails</a></p>" : ""
        );
    }

    private String buildReservationCancelledEmail(String recipientName, String actorName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #ffc107; color: black; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #ffc107; color: black; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚠️ Réservation annulée</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Une réservation a été annulée :</p>
                        <p><strong>%s</strong></p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir les détails</a></p>" : ""
        );
    }

    private String buildReservationOverdueEmail(String recipientName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #dc3545; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚨 Réservation en retard</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>URGENT - Votre réservation est en retard :</p>
                        <p><strong>%s</strong></p>
                        <p>Veuillez retourner l'équipement dès que possible.</p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir la réservation</a></p>" : ""
        );
    }

    private String buildMaintenanceUrgentEmail(String recipientName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #dc3545; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚨 Maintenance URGENTE</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Une maintenance urgente est requise :</p>
                        <p><strong>%s</strong></p>
                        <p>Veuillez traiter cette demande en priorité.</p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir les détails</a></p>" : ""
        );
    }

    private String buildMaintenanceHighEmail(String recipientName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #ffc107; color: black; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #ffc107; color: black; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚠️ Maintenance prioritaire</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Une maintenance prioritaire est nécessaire :</p>
                        <p><strong>%s</strong></p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir les détails</a></p>" : ""
        );
    }

    private String buildMaintenanceEmail(String recipientName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #17a2b8; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #17a2b8; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔧 Maintenance requise</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Une maintenance est requise :</p>
                        <p><strong>%s</strong></p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir les détails</a></p>" : ""
        );
    }

    private String buildDefaultEmail(String recipientName, String message, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #6c757d; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { background-color: #6c757d; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📩 Notification</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>%s</p>
                        %s
                    </div>
                    <div class="footer">
                        <p>Amara - Système de gestion d'équipements</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                recipientName,
                message,
                link != null ? "<p><a href=\"" + link + "\" class=\"button\">Voir les détails</a></p>" : ""
        );
    }

    public void sendNewDeviceLoginNotification(User user, String ipAddress, String userAgent) {

        // 1. Générer le token de déconnexion d'urgence
        String logoutToken = jwtService.generateEmergencyLogoutToken(user);

        // 2. Construire l'URL de déconnexion
        // Note : "your-frontend-url.com" doit être configurable
        String logoutUrl = "http://localhost:4200/security/emergency-logout?token=" + logoutToken;

        String subject = "Alerte de sécurité : Nouvelle connexion détectée sur votre compte Agrimate";
        String body = String.format(
                """
                Bonjour,
    
                Une nouvelle connexion à votre compte Agrimate a été détectée depuis un appareil ou un navigateur que nous ne reconnaissons pas.
    
                Détails de la connexion :
                - Appareil : %s
                - Adresse IP : %s
                - Heure : %s
    
                Si c'était bien vous, vous pouvez ignorer cet e-mail.
    
                Si vous ne reconnaissez pas cette activité, nous vous recommandons de changer votre mot de passe immédiatement pour sécuriser votre compte.
    
                Cordialement,
                L'équipe Agrimate
                """,
                user.getFirstName(),
                userAgent,
                ipAddress,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                logoutUrl
        );

        sendSimpleMessage(user.getEmail(), subject, body);
    }

    // Votre méthode d'envoi d'email
    private void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@agrimate.com"); // Mieux de configurer ça dans application.yml
            message.setTo(to);

            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException exception) {
            // Logguer l'erreur
            log.error("Failed to send email to {}", to, exception);
        }
    }
}