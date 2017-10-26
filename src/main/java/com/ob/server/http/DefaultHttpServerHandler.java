package com.ob.server.http;


import com.ob.server.ServerLogger;
import com.ob.server.resolvers.ResponderResolver;
import com.ob.server.session.ChannelUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObject;

import static com.ob.server.http.PrintUtil.fromStack;


class DefaultHttpServerHandler extends SimpleChannelInboundHandler<Object> {

   public final ResponderResolver responderResolver;
   public final ChannelGroup allChannels;
   private HttpConnect handler;

   public DefaultHttpServerHandler(ResponderResolver responderResolver, ChannelGroup allChannels) {
      this.responderResolver = responderResolver;
      this.allChannels = allChannels;
   }
   
   @Override
   public void channelRead0(final ChannelHandlerContext ctx, Object msg) {
       try {
           if (msg instanceof HttpObject) {
               if (handler == null || !handler.connected)
                   handler = HttpConnect.state(msg, handler,ctx.channel().id().asShortText());

               if (handler != null && handler.connected) {
                   ChannelUtil.channelRequest(ctx, handler.context, responderResolver);
               }
           }
       }catch (Exception e){
           ServerLogger.loggerProblem.error(String.format("Operation read, channel %s, error % ",ctx.channel().id().asShortText(), fromStack(e)));
       }

   }

   @Override
   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
       ServerLogger.loggerChannel.debug(String.format("Channel %s registered.", ctx.channel().id().asShortText()));
       ChannelUtil.gather(ctx, allChannels);
       ctx.fireChannelRegistered();
   }
   @Override
   public void exceptionCaught(final ChannelHandlerContext ctx, Throwable cause) {
       ServerLogger.loggerProblem.error(String.format("Channel %s, error % ",ctx.channel().id().asShortText(), fromStack(cause)));
       ctx.fireExceptionCaught(cause);
   }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
       ServerLogger.loggerChannel.debug(String.format("Channel %s unregistered.", ctx.channel().id().asShortText()));
       ctx.fireChannelUnregistered();
    }


}
