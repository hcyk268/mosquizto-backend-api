package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "smtp")
public class SMTPEmailImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${endpoint.confirmUser}")
    private String endpointConfirmUser;

    @Async
    @Override
    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailFrom, "Mosquizto Company");

        if (recipients.contains(",")) {
            helper.setTo(InternetAddress.parse(recipients));
        } else {
            helper.setTo(recipients);
        }

        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

        return "Sent email success to " + recipients;
    }

    @Async
    @Override
    public void sendConfirmLink(String emailTo, long userId, String fullName, String verifyCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Context context = new Context();

            // http://localhost:80/users/confirm/{userId}?verifyCode={verifyCode}
            String linkConfirm = String.format("%s/%s?verifyCode=%s", endpointConfirmUser, userId, verifyCode);

            Map<String, Object> properties = new HashMap<>();
            properties.put("confirmUrl", linkConfirm);
            properties.put("fullName", fullName);
            context.setVariables(properties);

            helper.setFrom(emailFrom, "Mosquizto Company");
            helper.setTo(emailTo);
            helper.setSubject("Please confirm your account");
            String html = templateEngine.process("confirm-account.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Confirmation email sent to {}", emailTo);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send confirmation email to {}: {}", emailTo, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendVerifyCode(String emailTo, String verCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Context context = new Context();

            Map<String, Object> properties = new HashMap<>();
            properties.put("verCode", verCode);
            context.setVariables(properties);

            helper.setFrom(emailFrom, "Mosquizto Company");
            helper.setTo(emailTo);
            helper.setSubject("Code Verify");
            String html = templateEngine.process("verify-code.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Verify code sent to {}", emailTo);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send email to {}: {}", emailTo, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCollectionShareInvite(String emailTo, String recipientName,
                                          String sharerUsername, String collectionTitle, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Context context = new Context();

            Map<String, Object> properties = new HashMap<>();
            properties.put("recipientName", recipientName);
            properties.put("sharerUsername", sharerUsername);
            properties.put("collectionTitle", collectionTitle);
            properties.put("role", formatRole(role));
            context.setVariables(properties);

            helper.setFrom(emailFrom, "Mosquizto Company");
            helper.setTo(emailTo);
            helper.setSubject(sharerUsername + " shared a collection with you");
            String html = templateEngine.process("collection-share-invite.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Collection share invite sent to {} for collection '{}'", emailTo, collectionTitle);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send collection share invite to {}: {}", emailTo, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCollectionReportNotification(String emailTo, String ownerName,
                                                 String reporterUsername, String collectionTitle, String reason, String description) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Context context = new Context();

            Map<String, Object> properties = new HashMap<>();
            properties.put("ownerName", ownerName);
            properties.put("reporterUsername", reporterUsername);
            properties.put("collectionTitle", collectionTitle);
            properties.put("reason", reason);
            properties.put("hasDescription", description != null && !description.isBlank());
            properties.put("description", description);
            context.setVariables(properties);

            helper.setFrom(emailFrom, "Mosquizto Company");
            helper.setTo(emailTo);
            helper.setSubject("Your collection \"" + collectionTitle + "\" has been reported");
            String html = templateEngine.process("collection-report-notification.html", context);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Collection report notification sent to {} for collection '{}'", emailTo, collectionTitle);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send collection report notification to {}: {}", emailTo, e.getMessage());
        }
    }

    /**
     * Chuyển role enum thành chuỗi thân thiện hơn cho email.
     */
    private String formatRole(String role) {
        return switch (role.toUpperCase()) {
            case "EDITOR" -> "Editor (can edit)";
            case "VIEWER" -> "Viewer (read-only)";
            default -> role;
        };
    }
}
