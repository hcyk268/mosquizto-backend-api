package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.MailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "sendgrid", matchIfMissing = true)
public class SendGridEmailImpl implements MailService {

    private final SendGrid sendGrid;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String emailFrom;

    @Value("${app.mail.from-name:Mosquizto Team}")
    private String fromName;

    @Value("${endpoint.confirmUser}")
    private String endpointConfirmUser;

    @Async
    @Override
    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws IOException {
        sendHtmlEmail(recipients, subject, content, files);
        return "Sent email success to " + recipients;
    }

    @Override
    public void sendConfirmLink(String emailTo, long userId, String fullName, String verifyCode) {
        try {
            String linkConfirm = String.format("%s/%s?verifyCode=%s", endpointConfirmUser, userId, verifyCode);

            Map<String, Object> properties = new HashMap<>();
            properties.put("confirmUrl", linkConfirm);
            properties.put("fullName", fullName);

            String html = renderTemplate("confirm-account.html", properties);
            sendHtmlEmail(emailTo, "Please confirm your account", html, null);
            log.info("Confirmation email sent to {}", emailTo);
        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}: {}", emailTo, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendVerifyCode(String emailTo, String verCode) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("verCode", verCode);

            String html = renderTemplate("verify-code.html", properties);
            sendHtmlEmail(emailTo, "Code Verify", html, null);
            log.info("Verify code sent to {}", emailTo);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", emailTo, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendCollectionShareInvite(String emailTo, String recipientName, String sharerUsername, String collectionTitle, String role) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("recipientName", recipientName);
            properties.put("sharerUsername", sharerUsername);
            properties.put("collectionTitle", collectionTitle);
            properties.put("role", formatRole(role));

            String html = renderTemplate("collection-share-invite.html", properties);
            sendHtmlEmail(emailTo, sharerUsername + " shared a collection with you", html, null);
            log.info("Collection share invite sent to {} for collection '{}'", emailTo, collectionTitle);
        } catch (Exception e) {
            log.error("Failed to send collection share invite to {}: {}", emailTo, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendCollectionReportNotification(String emailTo, String ownerName, String reporterUsername, String collectionTitle, String reason, String description) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("ownerName", ownerName);
            properties.put("reporterUsername", reporterUsername);
            properties.put("collectionTitle", collectionTitle);
            properties.put("reason", reason);
            properties.put("hasDescription", description != null && !description.isBlank());
            properties.put("description", description);

            String html = renderTemplate("collection-report-notification.html", properties);
            sendHtmlEmail(emailTo, "Your collection \"" + collectionTitle + "\" has been reported", html, null);
            log.info("Collection report notification sent to {} for collection '{}'", emailTo, collectionTitle);
        } catch (Exception e) {
            log.error("Failed to send collection report notification to {}: {}", emailTo, e.getMessage(), e);
        }
    }

    private String renderTemplate(String templateName, Map<String, Object> properties) {
        Context context = new Context();
        context.setVariables(properties);
        return templateEngine.process(templateName, context);
    }

    private void sendHtmlEmail(String recipients, String subject, String html, MultipartFile[] files) throws IOException {
        Mail mail = new Mail();
        mail.setFrom(new Email(emailFrom, fromName));
        mail.setSubject(subject);

        Content htmlContent = new Content("text/html", html);
        mail.addContent(htmlContent);

        for (String recipient : recipients.split(",")) {
            String trimmed = recipient.trim();
            if (!trimmed.isEmpty()) {
                mail.addPersonalization(buildPersonalization(trimmed));
            }
        }

        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    mail.addAttachments(buildAttachment(file));
                }
            }
        }

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            throw new IllegalStateException("SendGrid send failed: " + response.getStatusCode() + " - " + response.getBody());
        }
    }

    private Personalization buildPersonalization(String recipient) {
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(recipient));
        return personalization;
    }

    private Attachments buildAttachment(MultipartFile file) throws IOException {
        Attachments attachment = new Attachments();
        attachment.setFilename(file.getOriginalFilename());
        attachment.setType(file.getContentType());
        attachment.setDisposition("attachment");
        attachment.setContent(Base64.getEncoder().encodeToString(file.getBytes()));
        return attachment;
    }

    private String formatRole(String role) {
        return switch (role.toUpperCase()) {
            case "EDITOR" -> "Editor (can edit)";
            case "VIEWER" -> "Viewer (read-only)";
            default -> role;
        };
    }
}
