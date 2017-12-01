package com.ob.server.http;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class TokenAuthentication extends AuthenticationHandler{
    public TokenAuthentication(Access access) {
        super(access);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if(o instanceof HttpRequest){
            Object2ObjectArrayMap<String, String> params = HttpUtils.params((HttpRequest)o, null);
            if(!params.containsKey("token")){
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
                return;
            }
            if(!access.check(params.get("token"))){
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED);
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }
//        list.add(o);
        list.add(ReferenceCountUtil.retain(o));

    }
}
