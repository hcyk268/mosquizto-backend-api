package com.mosquizto.api.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Lúc này user vừa bắt tay Websocket thành công
        // Lấy thông tin user ra (nếu đã config Security/JWT)
        // Lưu vào Redis hoặc DB: User A đang ONLINE
        if (event.getUser() != null)  log.info(event.getUser().getName() + "has been connected");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // Mạng rớt, tắt tab, server tự động bắt được event này
        // Xóa trạng thái trong Redis: User A đã OFFLINE
        if (event.getUser() != null) log.info(event.getUser().getName() + " has been disconnected");
    }
}