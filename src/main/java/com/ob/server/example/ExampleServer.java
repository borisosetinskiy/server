package com.ob.server.example;

import com.ob.server.*;
import com.ob.server.handlers.GlobalErrorHandler;
import com.ob.server.handlers.HealthCheckHandler;
import com.ob.server.handlers.RateLimitHandler;
import com.ob.server.handlers.RequestTracingHandler;
import com.ob.server.security.JWTSecurityProcessor;
import com.ob.server.security.SecurityHandler;
import com.ob.server.session.RequestSession;
import com.ob.server.session.RequestSessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.cors.CorsConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Пример использования сервера с новыми возможностями
 */
@Slf4j
public class ExampleServer {
    
    public static void main(String[] args) {
        // Создаем фабрику сессий
        RequestSessionFactory sessionFactory = new ExampleSessionFactory();
        
        // Настраиваем CORS
        CorsConfig corsConfig = CorsConfig.withAnyOrigin()
            .allowedRequestMethods(io.netty.handler.codec.http.HttpMethod.GET, 
                                 io.netty.handler.codec.http.HttpMethod.POST,
                                 io.netty.handler.codec.http.HttpMethod.PUT,
                                 io.netty.handler.codec.http.HttpMethod.DELETE)
            .allowedRequestHeaders("Authorization", "Content-Type", "X-Request-ID")
            .allowCredentials()
            .build();
        
        // Создаем JWT процессор
        JWTSecurityProcessor jwtProcessor = new JWTSecurityProcessor("your-secret-key-here");
        SecurityHandler securityHandler = new SecurityHandler(jwtProcessor);
        
        // Создаем и настраиваем сервер
        Server server = new Server.ServerBuilder(8080, sessionFactory)
            .setWebsocket()  // Включаем WebSocket поддержку
            .setCorsConfig(corsConfig)
            .setSecurityHandler(securityHandler)
            .setReceiveBuffer(64 * 1024)      // 64KB receive buffer
            .setSendBuffer(128 * 1024)        // 128KB send buffer
            .setWriteBufferWaterMarkLow(16 * 1024)   // 16KB low water mark
            .setWriteBufferWaterMarkHigh(64 * 1024)  // 64KB high water mark
            .setBossNumber(1)                 // Один boss thread
            .setWorkNumber(4)                 // 4 worker threads
            .setHandlers(() -> new io.netty.channel.ChannelHandler[]{
                new RequestTracingHandler(),      // Трейсинг запросов
                new RateLimitHandler(1000.0),     // Rate limiting: 1000 req/sec
                new HealthCheckHandler("1.0.28", "example-server", new ExampleHealthChecker()),
                new GlobalErrorHandler()          // Глобальная обработка ошибок
            })
            .build();
        
        // Запускаем сервер
        server.start();
        
        log.info("Example server started on port 8080");
        log.info("Health check available at: http://localhost:8080/health");
        log.info("WebSocket endpoint available at: ws://localhost:8080/ws");
        
        // Добавляем graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Shutting down server...");
                server.stop();
                log.info("Server stopped gracefully");
            } catch (Exception e) {
                log.error("Error stopping server", e);
            }
        }));
    }
    
    /**
     * Пример фабрики сессий
     */
    static class ExampleSessionFactory implements RequestSessionFactory {
        @Override
        public RequestSession newRequestSession(ChannelRequestDto request) {
            return new ExampleSession(request);
        }
    }
    
    /**
     * Пример сессии
     */
    static class ExampleSession implements RequestSession {
        private final String sessionId;
        private final ChannelRequestDto request;
        
        public ExampleSession(ChannelRequestDto request) {
            this.sessionId = UUID.randomUUID().toString();
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
        
        @Override
        public void onOpen() {
            log.info("Session opened: {}", sessionId);
        }
        
        @Override
        public void onClose() {
            log.info("Session closed: {}", sessionId);
        }
        
        @Override
        public void onRead(ChannelHandlerContext ctx, Object msg) {
            log.info("Session {} received message: {}", sessionId, msg);
            
            // Здесь можно добавить обработку сообщений
            // Например, эхо-ответ
            if (msg instanceof String) {
                String response = "Echo: " + msg;
                ctx.writeAndFlush(response);
            }
        }
    }
    
    /**
     * Пример health checker
     */
    static class ExampleHealthChecker implements HealthCheckHandler.HealthStatusChecker {
        private boolean isReady = true;
        private boolean isAlive = true;
        
        @Override
        public boolean isReady() {
            // Здесь можно добавить проверки готовности
            // Например, проверка подключения к базе данных
            return isReady;
        }
        
        @Override
        public boolean isAlive() {
            // Здесь можно добавить проверки жизнеспособности
            return isAlive;
        }
        
        @Override
        public java.util.Map<String, Object> checkHealth() {
            return java.util.Map.of(
                "database", "connected",
                "cache", "available",
                "externalService", "healthy"
            );
        }
        
        // Методы для тестирования
        public void setReady(boolean ready) {
            this.isReady = ready;
        }
        
        public void setAlive(boolean alive) {
            this.isAlive = alive;
        }
    }
} 