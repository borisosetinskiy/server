package com.ob.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ob.server.health.HealthResponse;
import com.ob.server.health.HealthStatus;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик для health check endpoint
 */
@Slf4j
@ChannelHandler.Sharable
public class HealthCheckHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String HEALTH_PATH = "/health";
    private static final String HEALTH_DETAILED_PATH = "/health/detailed";
    private static final String READY_PATH = "/ready";
    private static final String LIVE_PATH = "/live";
    
    private final String version;
    private final String applicationName;
    private final HealthStatusChecker healthStatusChecker;
    
    public HealthCheckHandler() {
        this("1.0.28", "netty-server", null);
    }
    
    public HealthCheckHandler(String version, String applicationName) {
        this(version, applicationName, null);
    }
    
    public HealthCheckHandler(String version, String applicationName, HealthStatusChecker healthStatusChecker) {
        this.version = version;
        this.applicationName = applicationName;
        this.healthStatusChecker = healthStatusChecker;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        
        try {
            if (HEALTH_PATH.equals(uri)) {
                handleBasicHealth(ctx);
            } else if (HEALTH_DETAILED_PATH.equals(uri)) {
                handleDetailedHealth(ctx);
            } else if (READY_PATH.equals(uri)) {
                handleReadiness(ctx);
            } else if (LIVE_PATH.equals(uri)) {
                handleLiveness(ctx);
            } else {
                // Если это не health endpoint, передаем дальше
                ctx.fireChannelRead(request.retain());
            }
        } catch (Exception e) {
            log.error("Error in health check handler", e);
            sendErrorResponse(ctx, "Health check failed");
        }
    }
    
    private void handleBasicHealth(ChannelHandlerContext ctx) throws Exception {
        HealthResponse health = HealthResponse.builder()
            .status(HealthStatus.UP)
            .timestamp(Instant.now().toEpochMilli())
            .version(version)
            .application(applicationName)
            .build();
        
        sendHealthResponse(ctx, health, HttpResponseStatus.OK);
    }
    
    private void handleDetailedHealth(ChannelHandlerContext ctx) throws Exception {
        Map<String, Object> details = new HashMap<>();
        
        // Системная информация
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        details.put("system", Map.of(
            "cpuLoad", osBean.getSystemLoadAverage(),
            "availableProcessors", osBean.getAvailableProcessors(),
            "totalMemory", memoryBean.getHeapMemoryUsage().getMax(),
            "usedMemory", memoryBean.getHeapMemoryUsage().getUsed(),
            "freeMemory", memoryBean.getHeapMemoryUsage().getMax() - memoryBean.getHeapMemoryUsage().getUsed()
        ));
        
        // JVM информация
        details.put("jvm", Map.of(
            "uptime", ManagementFactory.getRuntimeMXBean().getUptime(),
            "startTime", ManagementFactory.getRuntimeMXBean().getStartTime(),
            "threadCount", ManagementFactory.getThreadMXBean().getThreadCount()
        ));
        
        // Кастомные проверки здоровья
        if (healthStatusChecker != null) {
            details.put("custom", healthStatusChecker.checkHealth());
        }
        
        HealthResponse health = HealthResponse.builder()
            .status(HealthStatus.UP)
            .timestamp(Instant.now().toEpochMilli())
            .version(version)
            .application(applicationName)
            .details(details)
            .build();
        
        sendHealthResponse(ctx, health, HttpResponseStatus.OK);
    }
    
    private void handleReadiness(ChannelHandlerContext ctx) throws Exception {
        HealthStatus status = HealthStatus.UP;
        String message = "Application is ready";
        
        // Проверяем готовность приложения
        if (healthStatusChecker != null && !healthStatusChecker.isReady()) {
            status = HealthStatus.DOWN;
            message = "Application is not ready";
        }
        
        HealthResponse health = HealthResponse.builder()
            .status(status)
            .timestamp(Instant.now().toEpochMilli())
            .version(version)
            .application(applicationName)
            .message(message)
            .build();
        
        HttpResponseStatus httpStatus = status == HealthStatus.UP ? 
            HttpResponseStatus.OK : HttpResponseStatus.SERVICE_UNAVAILABLE;
        
        sendHealthResponse(ctx, health, httpStatus);
    }
    
    private void handleLiveness(ChannelHandlerContext ctx) throws Exception {
        HealthStatus status = HealthStatus.UP;
        String message = "Application is alive";
        
        // Проверяем жизнеспособность приложения
        if (healthStatusChecker != null && !healthStatusChecker.isAlive()) {
            status = HealthStatus.DOWN;
            message = "Application is not alive";
        }
        
        HealthResponse health = HealthResponse.builder()
            .status(status)
            .timestamp(Instant.now().toEpochMilli())
            .version(version)
            .application(applicationName)
            .message(message)
            .build();
        
        HttpResponseStatus httpStatus = status == HealthStatus.UP ? 
            HttpResponseStatus.OK : HttpResponseStatus.SERVICE_UNAVAILABLE;
        
        sendHealthResponse(ctx, health, httpStatus);
    }
    
    private void sendHealthResponse(ChannelHandlerContext ctx, HealthResponse health, HttpResponseStatus status) throws Exception {
        String json = objectMapper.writeValueAsString(health);
        
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            Unpooled.copiedBuffer(json, StandardCharsets.UTF_8)
        );
        
        response.headers().set("Content-Type", "application/json");
        response.headers().set("Content-Length", json.length());
        response.headers().set("Cache-Control", "no-cache, no-store, must-revalidate");
        response.headers().set("Pragma", "no-cache");
        response.headers().set("Expires", "0");
        
        ctx.writeAndFlush(response);
    }
    
    private void sendErrorResponse(ChannelHandlerContext ctx, String message) {
        HealthResponse health = HealthResponse.builder()
            .status(HealthStatus.DOWN)
            .timestamp(Instant.now().toEpochMilli())
            .version(version)
            .application(applicationName)
            .message(message)
            .build();
        
        try {
            String json = objectMapper.writeValueAsString(health);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer(json, StandardCharsets.UTF_8)
            );
            
            response.headers().set("Content-Type", "application/json");
            response.headers().set("Content-Length", json.length());
            
            ctx.writeAndFlush(response);
        } catch (Exception e) {
            log.error("Failed to serialize error response", e);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer("Health check failed", StandardCharsets.UTF_8)
            );
            ctx.writeAndFlush(response);
        }
    }
    
    /**
     * Интерфейс для кастомных проверок здоровья
     */
    public interface HealthStatusChecker {
        /**
         * Проверить готовность приложения
         */
        default boolean isReady() {
            return true;
        }
        
        /**
         * Проверить жизнеспособность приложения
         */
        default boolean isAlive() {
            return true;
        }
        
        /**
         * Получить детальную информацию о здоровье
         */
        default Map<String, Object> checkHealth() {
            return Map.of();
        }
    }
} 