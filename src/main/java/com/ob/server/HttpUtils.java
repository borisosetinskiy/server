
package com.ob.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class HttpUtils {
    public static final String PATH = "P";

    public static void sendErrorAndCloseChannel(ChannelRequestDto channelRequestDto, HttpResponseStatus status) {
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        HttpUtils.sendResponseAndCloseChannel(channelRequestDto, (HttpResponse) httpResponse);
    }

    public static void sendErrorWithTextAndCloseChannel(ChannelRequestDto channelRequestDto, HttpResponseStatus status, String text) {
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer((byte[]) text.getBytes(CharsetUtil.UTF_8)));
        HttpUtils.sendResponseAndCloseChannel(channelRequestDto, (HttpResponse) httpResponse);
    }

    public static void sendResponseAndCloseChannel(ChannelRequestDto channelRequestDto, HttpResponse response) {
        channelRequestDto.getChannelContext().writeAndFlush((Object) response).addListener(ChannelFutureListener.CLOSE);
    }

    public static Object2ObjectArrayMap params(HttpObject o, Object2ObjectArrayMap params) {
        HttpContent content;
        if (params == null) {
            params = new Object2ObjectArrayMap();
        }
        if (o instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) o;
            int pathEndPos = httpRequest.uri().indexOf(63);
            if (pathEndPos != -1) {
                if (pathEndPos >= 0 && pathEndPos < httpRequest.uri().length() - 1) {
                    RequestUtil.decodeParams(httpRequest.uri().substring(pathEndPos + 1), params, HttpConstants.DEFAULT_CHARSET);
                }
            } else if (!httpRequest.uri().isEmpty()) {
                RequestUtil.decodeParams(httpRequest.uri(), params, HttpConstants.DEFAULT_CHARSET);
            }
            params.put(PATH, RequestUtil.decodeComponent(pathEndPos < 0 ? httpRequest.uri() : httpRequest.uri().substring(0, pathEndPos), HttpConstants.DEFAULT_CHARSET));
            if (httpRequest.headers() != null) {
                for (Map.Entry entry : httpRequest.headers()) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
        } else if (o instanceof HttpContent && (content = (HttpContent) o).content() != null && content.content().isReadable()) {
            byte[] buffer = new byte[content.content().readableBytes()];
            content.content().readBytes(buffer);
            RequestUtil.decodeParams(new String(buffer), params);
        }
        return params;
    }

    public static Object2ObjectArrayMap decodeParam(HttpContent content, Object2ObjectArrayMap<String, String> params) {
        if (params == null) {
            params = new Object2ObjectArrayMap();
        }
        if (content.content() != null && content.content().isReadable()) {
            byte[] buffer = new byte[content.content().readableBytes()];
            content.content().readBytes(buffer);
            RequestUtil.decodeParams(new String(buffer), params);
        }
        return params;
    }

    public static Object safeDuplicate(Object message) {
        return message instanceof ByteBuf ? ((ByteBuf) message).retainedDuplicate() : (message instanceof ByteBufHolder ? ((ByteBufHolder) message).retainedDuplicate() : ReferenceCountUtil.retain((Object) message));
    }
}

