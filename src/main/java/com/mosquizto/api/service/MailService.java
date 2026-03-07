package com.mosquizto.api.service;

import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

public interface MailService {

        String sendEmail(String recipients, String subject, String content, MultipartFile[] files)
                        throws MessagingException, UnsupportedEncodingException;

        void sendConfirmLink(String emailTo, long userId, String fullName, String verifyCode);
}
