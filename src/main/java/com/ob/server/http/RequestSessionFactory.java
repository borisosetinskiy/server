package com.ob.server.http;

import akka.actor.ActorSystem;
import com.ob.server.resolvers.ChannelRequest;

/**
 * Created by boris on 11.04.2016.
 */
public interface RequestSessionFactory {
    RequestSession getRequestSession(ChannelRequest channelRequest, ActorSystem system);
}
