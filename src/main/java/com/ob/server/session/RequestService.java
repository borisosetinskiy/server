/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server.session;

import com.ob.server.ChannelRequestDto;

public interface RequestService {
    RequestSession process(ChannelRequestDto var1) throws Exception;
}

