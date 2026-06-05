package com.mosquizto.api.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebsocketTest {

    @Test
    void writeSomeThing() throws Exception {
        // 1. Phải bọc StandardWebSocketClient vào SockJsClient vì server dùng .withSockJS()
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter()); // Cấu hình gửi/nhận String

        // Sửa lại URL cho chuẩn
        String url = "ws://localhost:8080/ws-connection";

        // Dùng cái này để chặn không cho hàm test kết thúc sớm
        CountDownLatch latch = new CountDownLatch(1);

        stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("Session user 1 active: " + session.getSessionId());

                // Sửa lại cho CHUẨN tên topic (/topic/invitation)
                session.subscribe("/topic/invitation", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println("Received from Server: " + payload);
                        latch.countDown(); // Nhận được tin rồi thì nhả chốt cho test pass
                    }
                });

                // Không gửi param lên URL, gửi qua Payload (tham số thứ 2)
                session.send("/mosquizto/send", "Hello từ Client nè!");
            }
        }).get();

        // Đợi tối đa 5 giây cho tin nhắn gửi đi và nhận lại, nếu không nhận được test sẽ fail
        boolean received = latch.await(5, TimeUnit.SECONDS);
        if (!received) {
            throw new RuntimeException("Test quá thời gian, không nhận được message!");
        }
    }
}