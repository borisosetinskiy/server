package com.ob.server.http;



import com.ob.server.resolvers.ChannelRequest;
import io.netty.channel.ChannelHandlerContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Arrays;

public class ChannelRequestImpl implements ChannelRequest {
   private ChannelHandlerContext ctx;
   private Object2ObjectArrayMap<String, String> context;

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
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append("ChannelRequestImpl{ctx=").append(ctx).append(", context=");
      builder.append(Arrays.toString(context.keySet().toArray())).append(':').append(Arrays.toString(context.values().toArray())).append('}');
      return  builder.toString();
   }

}
