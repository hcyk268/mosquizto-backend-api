package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${endpoint.confirmUser}")
    private String endpointConfirmUser;

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

            // http://localhost:80/user/confirm/{userId}?verifyCode={verifyCode}
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
}
