package com.mosquizto.api.service.impl;

import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.EmbeddingService;
import com.mosquizto.api.service.VectorStoreService;
import io.qdrant.client.QdrantClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebsocketTest {

    @MockBean
    private CollectionSearchService collectionSearchService;

    @MockBean
    private EmbeddingService embeddingService;

    @MockBean
    private VectorStoreService vectorStoreService;

    @MockBean
    private QdrantClient qdrantClient;

    @LocalServerPort
    private int port;

    @Value("${spring.websocket.connect-endpoint}")
    private String connectEndpoint;

    private StompSession session;

    @AfterEach
    void tearDown() {
        if (this.session != null && this.session.isConnected()) {
            this.session.disconnect();
        }
    }

    @Test
    void shouldConnectToWebsocketEndpoint() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        String url = "ws://localhost:" + this.port + this.connectEndpoint;

        this.session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                latch.countDown();
            }
        }).get(5, TimeUnit.SECONDS);

        assertTrue(this.session.isConnected());
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
