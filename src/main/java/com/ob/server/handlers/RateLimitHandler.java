package com.ob.server.handlers;

import com.google.common.util.concurrent.RateLimiter;
import com.ob.server.error.TooManyRequestException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Обработчик для ограничения скорости запросов (Rate Limiting)
 */
@Slf4j
@ChannelHandler.Sharable
public class RateLimitHandler extends ChannelInboundHandlerAdapter {
    
    private static final AttributeKey<String> CLIENT_ID_KEY = AttributeKey.valueOf("clientId");
    private final RateLimiter globalRateLimiter;
    private final ConcurrentMap<String, RateLimiter> clientRateLimiters;
    private final double requestsPerSecond;
    private final double clientRequestsPerSecond;
    
    public RateLimitHandler(double requestsPerSecond) {
        this(requestsPerSecond, requestsPerSecond / 10); // По умолчанию 10% от глобального лимита на клиента
    }
    
    public RateLimitHandler(double requestsPerSecond, double clientRequestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
        this.clientRequestsPerSecond = clientRequestsPerSecond;
        this.globalRateLimiter = RateLimiter.create(requestsPerSecond);
        this.clientRateLimiters = new ConcurrentHashMap<>();
        
        log.info("Rate limiting initialized: global={} req/sec, per-client={} req/sec", 
            requestsPerSecond, clientRequestsPerSecond);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            
            // Проверяем глобальный лимит
            if (!globalRateLimiter.tryAcquire()) {
                log.warn("Global rate limit exceeded for request: {}", request.uri());
                sendRateLimitResponse(ctx, "Global rate limit exceeded");
                return;
            }
            
            // Получаем идентификатор клиента
            String clientId = getClientId(request);
            ctx.channel().attr(CLIENT_ID_KEY).set(clientId);
            
            // Проверяем лимит для конкретного клиента
            RateLimiter clientLimiter = getClientRateLimiter(clientId);
            if (!clientLimiter.tryAcquire()) {
                log.warn("Client rate limit exceeded for client: {}, request: {}", clientId, request.uri());
                sendRateLimitResponse(ctx, "Client rate limit exceeded");
                return;
            }
            
            log.debug("Rate limit check passed for client: {}, request: {}", clientId, request.uri());
        }
        
        ctx.fireChannelRead(msg);
    }
    
    private String getClientId(FullHttpRequest request) {
        // Приоритет источников для идентификации клиента:
        // 1. X-Forwarded-For (если за прокси)
        // 2. X-Real-IP
        // 3. Remote address
        // 4. User-Agent (как fallback)
        
        String clientId = request.headers().get("X-Forwarded-For");
        if (clientId != null && !clientId.isEmpty()) {
            return clientId.split(",")[0].trim(); // Берем первый IP из списка
        }
        
        clientId = request.headers().get("X-Real-IP");
        if (clientId != null && !clientId.isEmpty()) {
            return clientId;
        }
        
        // Если нет заголовков, используем User-Agent как fallback
        clientId = request.headers().get("User-Agent");
        if (clientId != null && !clientId.isEmpty()) {
            return "ua:" + clientId.hashCode(); // Хешируем для экономии памяти
        }
        
        return "unknown";
    }
    
    private RateLimiter getClientRateLimiter(String clientId) {
        return clientRateLimiters.computeIfAbsent(clientId, 
            k -> RateLimiter.create(clientRequestsPerSecond));
    }
    
    private void sendRateLimitResponse(ChannelHandlerContext ctx, String message) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.TOO_MANY_REQUESTS
        );
        
        response.headers().set("Content-Type", "text/plain");
        response.headers().set("Retry-After", "1"); // Предлагаем повторить через 1 секунду
        response.headers().set("X-RateLimit-Limit", String.valueOf((int) requestsPerSecond));
        response.headers().set("X-RateLimit-Remaining", "0");
        response.headers().set("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + 1));
        
        ctx.writeAndFlush(response);
    }
    
    /**
     * Получить статистику по rate limiting
     */
    public RateLimitStats getStats() {
        return RateLimitStats.builder()
            .globalRequestsPerSecond(requestsPerSecond)
            .clientRequestsPerSecond(clientRequestsPerSecond)
            .activeClients(clientRateLimiters.size())
            .build();
    }
    
    /**
     * Очистить неактивных клиентов (для экономии памяти)
     */
    public void cleanupInactiveClients() {
        // Можно добавить логику очистки клиентов, которые не делали запросы долгое время
        log.debug("Rate limit cleanup: {} active clients", clientRateLimiters.size());
    }
    
    @lombok.Data
    @lombok.Builder
    public static class RateLimitStats {
        private double globalRequestsPerSecond;
        private double clientRequestsPerSecond;
        private int activeClients;
    }
} 