package com.ob.server.http.handler;

import com.ob.server.http.HttpUtils;
import com.ob.server.resolvers.Responder;
import com.ob.server.resolvers.ResponderResolver;
import com.ob.server.session.ChannelRequestImpl;
import com.ob.server.session.RequestSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ob.server.http.AttributeKeys.CHANNEL_REQUEST_ATTR_KEY;
import static com.ob.server.http.AttributeKeys.REQUEST_SESSION_ATTR_KEY;

public class RequestSessionHandler extends MessageToMessageDecoder<Object> {
    private final ResponderResolver responderResolver;
    private AtomicBoolean done = new AtomicBoolean();

    public RequestSessionHandler(ResponderResolver responderResolver) {
        this.responderResolver = responderResolver;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {

        if(!done.get()){
            ChannelRequestImpl  channelRequest = channelHandlerContext.channel().attr(CHANNEL_REQUEST_ATTR_KEY).get();
            if(channelRequest != null) {
                try {
                    String path = channelRequest.getContext().get(HttpUtils.PATH);
                    if (!channelRequest.getContext().isEmpty()) {
                        final Responder responder = responderResolver.resolve(path);
                        if (responder != null) {
                            RequestSession requestSession = responder.respond(channelRequest.copy(channelHandlerContext));
                            if (requestSession != null)
                                channelHandlerContext.channel().attr(REQUEST_SESSION_ATTR_KEY).set(requestSession);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    done.set(true);
                }
            }
        }

    }
}
