package com.mosquizto.api.service;

import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface MailService {

        String sendEmail(String recipients, String subject, String content, MultipartFile[] files)
                throws MessagingException, IOException;

        void sendConfirmLink(String emailTo, long userId, String fullName, String verifyCode);

        void sendVerifyCode(String emailTo, String verCode);

        void sendCollectionShareInvite(String emailTo, String recipientName,
                                       String sharerUsername, String collectionTitle, String role);
        void sendCollectionReportNotification(String emailTo, String ownerName,
                                              String reporterUsername, String collectionTitle, String reason, String description);
}
