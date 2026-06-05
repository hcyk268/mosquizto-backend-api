package com.mosquizto.api.configuration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@RequiredArgsConstructor
public class MessagingTempleConfig {
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${spring.websocket.message-time-out}")
    private Long timeout; // ms

    @PostConstruct
    public void customMessagingTemplate() {
        this.messagingTemplate.setSendTimeout(timeout);
    }
}
