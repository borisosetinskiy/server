
package com.ob.server.session;

import com.ob.server.ChannelRequestDto;
import io.netty.channel.ChannelHandlerContext;

public interface RequestSession {
    default void onWrite(Object var1) {
    }

    String getSessionId();

    default void onOpen() {
    }

    default void onClose() {
    }

    default void onRead(ChannelHandlerContext channelHandlerContext, Object var1) {
    }

    ChannelRequestDto getChannelRequest();

}

