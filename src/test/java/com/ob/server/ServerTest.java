package com.ob.server;

import com.ob.server.session.RequestSession;
import com.ob.server.session.RequestSessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Базовые тесты для сервера
 */
public class ServerTest {
    
    private Server server;
    private TestSessionFactory sessionFactory;
    
    @BeforeEach
    void setUp() {
        sessionFactory = new TestSessionFactory();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
    
    @Test
    void testServerCreation() {
        server = new Server.ServerBuilder(8080, sessionFactory)
            .setHttp()
            .build();
        
        assertNotNull(server);
    }
    
    @Test
    void testServerStart() {
        server = new Server.ServerBuilder(8080, sessionFactory)
            .setHttp()
            .build();
        
        server.start();
        
        // Сервер должен запуститься без исключений
        assertNotNull(server);
    }
    
    @Test
    void testWebSocketServerCreation() {
        server = new Server.ServerBuilder(8080, sessionFactory)
            .setWebsocket()
            .build();
        
        assertNotNull(server);
    }
    
    @Test
    void testServerWithCustomConfig() {
        server = new Server.ServerBuilder(8080, sessionFactory)
            .setHttp()
            .setReceiveBuffer(64 * 1024)
            .setSendBuffer(128 * 1024)
            .setBossNumber(1)
            .setWorkNumber(2)
            .build();
        
        assertNotNull(server);
    }
    
    /**
     * Тестовая фабрика сессий
     */
    static class TestSessionFactory implements RequestSessionFactory {
        @Override
        public RequestSession newRequestSession(ChannelRequestDto request) {
            return new TestSession(request);
        }
    }
    
    /**
     * Тестовая сессия
     */
    static class TestSession implements RequestSession {
        private final String sessionId;
        private final ChannelRequestDto request;
        
        public TestSession(ChannelRequestDto request) {
            this.sessionId = "test-session-" + System.currentTimeMillis();
            this.request = request;
        }
        
        @Override
        public String getSessionId() {
            return sessionId;
        }
        
        @Override
        public ChannelRequestDto getChannelRequest() {
            return request;
        }
    }
} 