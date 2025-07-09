package com.ob.server.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Обработчик для трейсинга запросов с использованием MDC
 */
@Slf4j
@ChannelHandler.Sharable
public class RequestTracingHandler extends ChannelInboundHandlerAdapter {
    
    private static final AttributeKey<String> REQUEST_ID_KEY = AttributeKey.valueOf("requestId");
    private static final AttributeKey<String> TRACE_ID_KEY = AttributeKey.valueOf("traceId");
    private static final AttributeKey<Long> START_TIME_KEY = AttributeKey.valueOf("startTime");
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            
            // Генерируем уникальные идентификаторы
            String requestId = UUID.randomUUID().toString();
            String traceId = UUID.randomUUID().toString();
            long startTime = System.currentTimeMillis();
            
            // Сохраняем в атрибутах канала
            ctx.channel().attr(REQUEST_ID_KEY).set(requestId);
            ctx.channel().attr(TRACE_ID_KEY).set(traceId);
            ctx.channel().attr(START_TIME_KEY).set(startTime);
            
            // Устанавливаем MDC для логирования
            MDC.put("requestId", requestId);
            MDC.put("traceId", traceId);
            MDC.put("method", request.method().name());
            MDC.put("uri", request.uri());
            MDC.put("userAgent", request.headers().get("User-Agent", "unknown"));
            MDC.put("clientIp", getClientIp(request));
            
            log.info("Request started: {} {}", request.method(), request.uri());
        }
        
        ctx.fireChannelRead(msg);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // Логируем завершение обработки запроса
        String requestId = ctx.channel().attr(REQUEST_ID_KEY).get();
        String traceId = ctx.channel().attr(TRACE_ID_KEY).get();
        Long startTime = ctx.channel().attr(START_TIME_KEY).get();
        
        if (requestId != null && startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request completed in {}ms", duration);
            
            // Очищаем MDC
            MDC.clear();
        }
        
        ctx.fireChannelReadComplete();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String requestId = ctx.channel().attr(REQUEST_ID_KEY).get();
        String traceId = ctx.channel().attr(TRACE_ID_KEY).get();
        
        log.error("Request failed: {}", cause.getMessage(), cause);
        
        // Очищаем MDC
        MDC.clear();
        
        ctx.fireExceptionCaught(cause);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // Очищаем MDC при закрытии соединения
        MDC.clear();
        ctx.fireChannelInactive();
    }
    
    private String getClientIp(FullHttpRequest request) {
        // Приоритет источников для получения IP клиента
        String clientIp = request.headers().get("X-Forwarded-For");
        if (clientIp != null && !clientIp.isEmpty()) {
            return clientIp.split(",")[0].trim();
        }
        
        clientIp = request.headers().get("X-Real-IP");
        if (clientIp != null && !clientIp.isEmpty()) {
            return clientIp;
        }
        
        clientIp = request.headers().get("X-Client-IP");
        if (clientIp != null && !clientIp.isEmpty()) {
            return clientIp;
        }
        
        return "unknown";
    }
    
    /**
     * Получить requestId из контекста канала
     */
    public static String getRequestId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(REQUEST_ID_KEY).get();
    }
    
    /**
     * Получить traceId из контекста канала
     */
    public static String getTraceId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(TRACE_ID_KEY).get();
    }
    
    /**
     * Получить время начала запроса из контекста канала
     */
    public static Long getStartTime(ChannelHandlerContext ctx) {
        return ctx.channel().attr(START_TIME_KEY).get();
    }
} 