package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendInvitationToSpecificUser(String targetUsername, String message) {
        // Gửi đích danh. Spring sẽ lén thêm username vào URL ngầm bên dưới
        this.messagingTemplate.convertAndSendToUser(
                targetUsername,
                "/queue/invitation", // Đích đến tự động :/usser/{username}/queue/invitation
                message
        );
        log.info(message);
        // 💡 BÊN FE CHỈ CẦN SUBSCRIBE ĐÚNG CÁI NÀY:
        // stompClient.subscribe('/user/queue/invitation', function(msg) {...})
    }

    @Override
    public void sendReportToSpecificUser(String targetUsername, String message) {
        this.messagingTemplate.convertAndSendToUser(
                targetUsername,
                "/queue/report",
                message
        );
        log.info(message);
    }
}