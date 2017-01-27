package com.ob.server.http;

import com.ob.server.resolvers.ChannelRequest;

/**
 * Created by boris on 11.04.2016.
 */
public interface RequestService {
    RequestSession process(final ChannelRequest channelRequest)throws Exception;
}
