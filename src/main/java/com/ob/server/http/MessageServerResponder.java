package com.ob.server.http;


import com.ob.server.resolvers.ChannelRequest;
import com.ob.server.resolvers.Responder;
import org.springframework.beans.factory.annotation.Required;


public class MessageServerResponder implements Responder {
   private RequestService requestService;
   @Required
   public void setRequestService(RequestService requestService) {
      this.requestService = requestService;
   }

   @Override
   public RequestSession respond(ChannelRequest channelRequest)throws Exception {
      return  requestService.process(channelRequest);
   }
}