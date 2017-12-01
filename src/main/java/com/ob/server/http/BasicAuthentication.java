package com.ob.server.http;


import com.google.common.net.HttpHeaders;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import sun.misc.BASE64Decoder;

import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class BasicAuthentication extends AuthenticationHandler{
    private static final String BASIC_AUTHENTICATION_REGEX = "Basic\\s";
    private static final String EMPTY_STRING = "";
    private static final String USERNAME_PASSWORD_SEPARATOR = ":";
    private static final String WWW_AUTHENTICATE_HEADER_VALUE = "Basic realm=\"Default realm\"";

    private static final BASE64Decoder DECODER = new BASE64Decoder();

    public BasicAuthentication(Access access) {
        super(access);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        if(o instanceof HttpRequest){
            Object2ObjectArrayMap<String, String> params = HttpUtils.params((HttpRequest)o, null);
            if(!params.containsKey(HttpHeaders.AUTHORIZATION) ){
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
                return;
            }
            String authHeader = params.get(HttpHeaders.AUTHORIZATION);
            authHeader = authHeader.replaceFirst(BASIC_AUTHENTICATION_REGEX, EMPTY_STRING);
            authHeader = new String(DECODER.decodeBuffer(authHeader));
            String[] creds = authHeader.split(USERNAME_PASSWORD_SEPARATOR);
            String username = creds[0];
            String password = creds[1];

            if(!access.check(new UserDetails(username, password))){
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED);
                response.headers().add(HttpHeaders.WWW_AUTHENTICATE, WWW_AUTHENTICATE_HEADER_VALUE);
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;

            }

        }
//        list.add(o);
        list.add(ReferenceCountUtil.retain(o));
    }
}
