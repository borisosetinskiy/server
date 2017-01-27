package com.ob.server.http;

import com.ob.common.actor.WithActorService;
import com.ob.server.ServerLogger;
import com.ob.server.actor.RequestSessionActorWrapper;
import com.ob.server.resolvers.ChannelRequest;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Required;


/**
 * Created by boris on 11.04.2016.
 */
public class DefaultRequestService extends WithActorService implements RequestService {

    private RequestSessionFactory requestSessionFactory;

    private int ratio = 1;

    @Required
    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    @Required
    public void setRequestSessionFactory(RequestSessionFactory requestSessionFactory) {
        this.requestSessionFactory = requestSessionFactory;
    }

    protected void onSessionOpen(String key, RequestSession requestSession ){ }

    protected void onSessionClose(String key){ }

    @Override
    public RequestSession process(final ChannelRequest channelRequest )throws Exception {
        final Channel channel = channelRequest.getChannelContext().channel();
        ServerLogger.logger.debug("Channel:"+channel.id().asShortText()+":opened.");
        final RequestSession requestSession =
                new RequestSessionActorWrapper(
                        requestSessionFactory.getRequestSession(channelRequest, actorService.getActorSystem())
                        , actorService.getActorSystem()
                        , ratio
                );
        onSessionOpen(channel.id().asShortText(), requestSession);
        requestSession.onOpen();
        channel.closeFuture().addListener(future -> {
            requestSession.onClose();
            ServerLogger.logger.debug("Channel:"+channel.id().asShortText()+":closed.");
            onSessionClose(channel.id().asShortText());
        });
        return requestSession;
    }


}
