package com.ob.server;

public interface SocketFrameWrapper<IN, OUT> {
    OUT wrap(IN message);
}
