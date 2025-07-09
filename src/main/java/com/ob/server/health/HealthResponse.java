package com.ob.server.health;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Ответ для health check endpoint
 */
@Data
@Builder
public class HealthResponse {
    private HealthStatus status;
    private long timestamp;
    private String version;
    private String application;
    private String message;
    private Map<String, Object> details;
} 