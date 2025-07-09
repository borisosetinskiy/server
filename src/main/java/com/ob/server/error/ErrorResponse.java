package com.ob.server.error;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Стандартизированный ответ с ошибкой
 */
@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private String details;
    private String requestId;
    private String path;
    private long timestamp;
    private String traceId;
    
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
            .code(code)
            .message(message)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }
    
    public static ErrorResponse of(String code, String message, String details) {
        return ErrorResponse.builder()
            .code(code)
            .message(message)
            .details(details)
            .timestamp(Instant.now().toEpochMilli())
            .build();
    }
    
    public static ErrorResponse badRequest(String message) {
        return of("BAD_REQUEST", message);
    }
    
    public static ErrorResponse unauthorized(String message) {
        return of("UNAUTHORIZED", message);
    }
    
    public static ErrorResponse forbidden(String message) {
        return of("FORBIDDEN", message);
    }
    
    public static ErrorResponse notFound(String message) {
        return of("NOT_FOUND", message);
    }
    
    public static ErrorResponse tooManyRequests(String message) {
        return of("TOO_MANY_REQUESTS", message);
    }
    
    public static ErrorResponse internalError(String message) {
        return of("INTERNAL_ERROR", message);
    }
    
    // Методы для установки дополнительных полей
    public ErrorResponse requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    
    public ErrorResponse traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
    
    public ErrorResponse path(String path) {
        this.path = path;
        return this;
    }
    
    public ErrorResponse details(String details) {
        this.details = details;
        return this;
    }
} 