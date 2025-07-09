package com.ob.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ob.server.error.BadRequestException;
import com.ob.server.error.ErrorResponse;
import com.ob.server.error.ForbiddenException;
import com.ob.server.error.ProtocolException;
import com.ob.server.error.TooManyRequestException;
import com.ob.server.error.UnauthorizedException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Глобальный обработчик ошибок для централизованной обработки исключений
 */
@Slf4j
@ChannelHandler.Sharable
public class GlobalErrorHandler extends ChannelInboundHandlerAdapter {
    
    private static final AttributeKey<String> REQUEST_ID_KEY = AttributeKey.valueOf("requestId");
    private static final AttributeKey<String> TRACE_ID_KEY = AttributeKey.valueOf("traceId");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String requestId = getOrCreateRequestId(ctx);
        String traceId = getOrCreateTraceId(ctx);
        
        log.error("Error processing request {} with trace {}: {}", 
            requestId, traceId, cause.getMessage(), cause);
        
        ErrorResponse errorResponse = buildErrorResponse(cause, requestId, traceId);
        
        try {
            String errorJson = objectMapper.writeValueAsString(errorResponse);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                getHttpStatus(cause),
                Unpooled.copiedBuffer(errorJson, StandardCharsets.UTF_8)
            );
            
            response.headers().set("Content-Type", "application/json");
            response.headers().set("Content-Length", errorJson.length());
            response.headers().set("X-Request-ID", requestId);
            response.headers().set("X-Trace-ID", traceId);
            
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            
        } catch (Exception e) {
            log.error("Failed to serialize error response", e);
            // Fallback to simple error response
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.copiedBuffer("Internal Server Error", StandardCharsets.UTF_8)
            );
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String requestId = getOrCreateRequestId(ctx);
            String traceId = getOrCreateTraceId(ctx);
            
            // Добавляем requestId и traceId в атрибуты канала
            ctx.channel().attr(REQUEST_ID_KEY).set(requestId);
            ctx.channel().attr(TRACE_ID_KEY).set(traceId);
            
            log.debug("Processing request {} with trace {}: {} {}", 
                requestId, traceId, request.method(), request.uri());
        }
        
        ctx.fireChannelRead(msg);
    }
    
    private String getOrCreateRequestId(ChannelHandlerContext ctx) {
        String requestId = ctx.channel().attr(REQUEST_ID_KEY).get();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
            ctx.channel().attr(REQUEST_ID_KEY).set(requestId);
        }
        return requestId;
    }
    
    private String getOrCreateTraceId(ChannelHandlerContext ctx) {
        String traceId = ctx.channel().attr(TRACE_ID_KEY).get();
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            ctx.channel().attr(TRACE_ID_KEY).set(traceId);
        }
        return traceId;
    }
    
    private ErrorResponse buildErrorResponse(Throwable cause, String requestId, String traceId) {
        if (cause instanceof BadRequestException) {
            return ErrorResponse.badRequest(cause.getMessage())
                .requestId(requestId)
                .traceId(traceId);
        } else if (cause instanceof UnauthorizedException) {
            return ErrorResponse.unauthorized(cause.getMessage())
                .requestId(requestId)
                .traceId(traceId);
        } else if (cause instanceof ForbiddenException) {
            return ErrorResponse.forbidden(cause.getMessage())
                .requestId(requestId)
                .traceId(traceId);
        } else if (cause instanceof TooManyRequestException) {
            return ErrorResponse.tooManyRequests(cause.getMessage())
                .requestId(requestId)
                .traceId(traceId);
        } else if (cause instanceof ProtocolException) {
            return ErrorResponse.badRequest(cause.getMessage())
                .requestId(requestId)
                .traceId(traceId);
        } else {
            return ErrorResponse.internalError("Internal server error")
                .requestId(requestId)
                .traceId(traceId)
                .details(cause.getMessage());
        }
    }
    
    private HttpResponseStatus getHttpStatus(Throwable cause) {
        if (cause instanceof BadRequestException) {
            return HttpResponseStatus.BAD_REQUEST;
        } else if (cause instanceof UnauthorizedException) {
            return HttpResponseStatus.UNAUTHORIZED;
        } else if (cause instanceof ForbiddenException) {
            return HttpResponseStatus.FORBIDDEN;
        } else if (cause instanceof TooManyRequestException) {
            return HttpResponseStatus.TOO_MANY_REQUESTS;
        } else if (cause instanceof ProtocolException) {
            return HttpResponseStatus.BAD_REQUEST;
        } else {
            return HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
    }
} 