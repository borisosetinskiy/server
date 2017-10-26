package com.ob.server.http;

import com.ob.server.resolvers.ChannelRequest;
import com.ob.server.resolvers.Responder;
import com.ob.server.session.RequestSession;
import io.netty.handler.codec.http.HttpResponseStatus;


public class NotFoundResponder implements Responder {

   @Override
   public RequestSession respond(ChannelRequest channelRequest)throws Exception {
      HttpUtil.sendErrorAndCloseChannel(channelRequest, HttpResponseStatus.NOT_FOUND);
      return null;
   }
}
