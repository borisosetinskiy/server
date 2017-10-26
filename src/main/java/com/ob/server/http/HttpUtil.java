package com.ob.server.http;


import com.ob.server.resolvers.ChannelRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by boris on 19.04.2016.
 */
public final class HttpUtil {

   public static void sendErrorAndCloseChannel(ChannelRequest channelRequest, HttpResponseStatus status) {
      DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status);
      sendResponseAndCloseChannel(channelRequest, httpResponse);
   }
   public static void sendErrorWithTextAndCloseChannel(ChannelRequest channelRequest, HttpResponseStatus status, String text) {
      DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.wrappedBuffer(text.getBytes(CharsetUtil.UTF_8) ));
      sendResponseAndCloseChannel(channelRequest, httpResponse);
   }

   public static void sendResponseAndCloseChannel(ChannelRequest channelRequest, HttpResponse response) {
	   channelRequest.getChannelContext().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
   }

   public static Object2ObjectArrayMap decodeParam(HttpContent content, Object2ObjectArrayMap<String, String> context){
      if(context == null){
         context = new Object2ObjectArrayMap();
      }
      if(content.content()!=null && content.content().isReadable()){
         final byte[] buffer = new byte[content.content().readableBytes()];
         content.content().readBytes(buffer);
         RequestUtil.decodeParams(new String(buffer), context);
      }
      return context;
   }
   public static Object safeDuplicate(Object message) {
      return message instanceof ByteBuf ?((ByteBuf)message).retainedDuplicate():(message instanceof ByteBufHolder ?((ByteBufHolder)message).retainedDuplicate(): ReferenceCountUtil.retain(message));
   }



}

