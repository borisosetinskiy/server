package com.ob.server.handlers.websocket;

import com.ob.server.SocketFrameWrapper;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketFrameWrapper<T> extends SocketFrameWrapper<T, WebSocketFrame> {
    WebSocketFrame wrap(T message);
}
