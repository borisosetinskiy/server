package com.ob.server.session;



import com.ob.server.http.HttpUtils;
import com.ob.server.resolvers.ChannelRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelRequestImpl implements ChannelRequest {
   private final ChannelHandlerContext ctx;
   private Object2ObjectArrayMap<String, String> context = new Object2ObjectArrayMap();
   private long timestamp = System.currentTimeMillis();

   public ChannelRequestImpl(ChannelHandlerContext ctx) {
      this.ctx = ctx;
   }

   private AtomicBoolean finished = new AtomicBoolean();
   public boolean isFinished() {
      return finished.get();
   }

   public void handle(Object msg) {
      if(!isFinished()) {
         if (msg instanceof HttpRequest) {
            context = HttpUtils.params((HttpObject)msg, context);
         }
         if (msg instanceof LastHttpContent) {
            HttpUtils.params((HttpObject)msg, context);
            finished.set(true);
         }
      }
   }
   public ChannelHandlerContext getChannelContext() {
      return ctx;
   }
   @Override
   public Object2ObjectArrayMap<String, String> getContext() {
      return context;
   }

   @Override
   public long getTimestamp() {
      return timestamp;
   }

   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append("{channel=").append(ctx.channel()).append(", context=");
      builder.append(Arrays.toString(context.keySet().toArray())).append(':')
              .append(Arrays.toString(context.values().toArray())).append('}');
      return  builder.toString();
   }

}
