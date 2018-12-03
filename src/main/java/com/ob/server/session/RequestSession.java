/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.session;

import com.ob.server.ChannelRequestDto;

public interface RequestSession {
    default void onWrite(Object var1) {
    }

    String getSessionId();

    default void onOpen() {
    }

    default void onClose() {
    }

    default void onRead(Object var1) {
    }

    ChannelRequestDto getChannelRequest();

}

