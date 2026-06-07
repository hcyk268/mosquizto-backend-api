package com.mosquizto.api.event.listener;

import com.mosquizto.api.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@AllArgsConstructor
public class WebSocketEventListener {
    private final NotificationService notificationService ;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Lúc này user vừa bắt tay Websocket thành công
        if (event.getUser() == null) return;

        String username = event.getUser().getName();
        log.info("{} connected — flushing unread notifications", username);

        // Chạy async để không block handshake
        notificationService.flushUnreadToUser(username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // Mạng rớt, tắt tab, server tự động bắt được event này
        // Xóa trạng thái trong Redis: User A đã OFFLINE
        if (event.getUser() != null) {
            log.info("{} disconnected", event.getUser().getName());
        }
    }
}