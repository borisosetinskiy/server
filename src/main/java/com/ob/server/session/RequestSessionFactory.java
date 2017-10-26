package com.ob.server.session;

import com.ob.server.resolvers.ChannelRequest;

/**
 * Created by boris on 11.04.2016.
 */
public interface RequestSessionFactory {
    RequestSession newRequestSession(ChannelRequest channelRequest);
}
