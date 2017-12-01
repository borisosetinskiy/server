package com.ob.server.http.websocket;

public interface WebSocketServerProtocolHandshakeHandlerFactory {
    WebSocketServerProtocolHandshakeHandler create(String websocketPath,
                                                   String subprotocols,
                                                   boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch);
}
