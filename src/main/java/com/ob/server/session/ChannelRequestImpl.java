package com.ob.server.session;



import com.ob.server.resolvers.ChannelRequest;
import io.netty.channel.ChannelHandlerContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Arrays;

public class ChannelRequestImpl implements ChannelRequest {
   private ChannelHandlerContext ctx;
   private Object2ObjectArrayMap<String, String> context;
   private long timestamp = System.currentTimeMillis();

   public ChannelRequestImpl(ChannelHandlerContext ctx, Object2ObjectArrayMap context) {
      this.ctx = ctx;
      this.context = context;
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
