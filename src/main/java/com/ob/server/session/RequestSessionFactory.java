/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.session;

import com.ob.server.ChannelRequestDto;

public interface RequestSessionFactory {
    RequestSession newRequestSession(ChannelRequestDto var1);
}

