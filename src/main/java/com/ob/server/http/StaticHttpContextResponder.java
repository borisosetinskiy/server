package com.ob.server.http;

import com.ob.server.resolvers.ChannelRequest;
import com.ob.server.resolvers.Responder;
import com.ob.server.session.RequestSession;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static com.ob.server.http.HttpUtils.sendResponseAndCloseChannel;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class StaticHttpContextResponder implements Responder {
    private final String context;
    private final String type;



    public StaticHttpContextResponder(String context, String type) {
        this.context = context;
        this.type = type;
    }

    @Override
    public RequestSession respond(ChannelRequest channelRequest) throws Exception {
        DefaultFullHttpResponse httpResponse = null;
        if(context!=null)
            httpResponse= new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(context.getBytes(CharsetUtil.UTF_8)));
        else
            httpResponse= new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
        httpResponse.headers().set(CONTENT_TYPE, type);
        sendResponseAndCloseChannel(channelRequest, httpResponse);
        return null;
    }
}
