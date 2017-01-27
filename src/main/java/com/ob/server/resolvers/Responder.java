package com.ob.server.resolvers;

import com.ob.server.http.RequestSession;

public interface Responder {
   RequestSession respond(ChannelRequest channelRequest)throws Exception;
}
