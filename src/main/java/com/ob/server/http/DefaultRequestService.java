package com.ob.server.http;

import com.ob.common.akka.WithActorService;
import com.ob.server.ServerLogger;
import com.ob.server.actor.RequestSessionActorWrapper;
import com.ob.server.resolvers.ChannelRequest;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;


/**
 * Created by boris on 11.04.2016.
 */
public class DefaultRequestService extends WithActorService implements RequestService {

    private RequestSessionFactory requestSessionFactory;
    private StatisticService statisticService;

    private int ratio = 1;
    private boolean wrapped = true;

    @Required
    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    public StatisticService getStatisticService() {
        return statisticService;
    }

    @Autowired
    public void setStatisticService(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Required
    public void setRequestSessionFactory(RequestSessionFactory requestSessionFactory) {
        this.requestSessionFactory = requestSessionFactory;
    }

    @PostConstruct
    public void init(){
        if(statisticService == null)
            statisticService = StatisticService.STATISTIC_SERVICE;
    }

    protected void onSessionOpen(String key, RequestSession requestSession ){ }

    protected void onSessionClose(String key){ }

    @Override
    public RequestSession process(final ChannelRequest channelRequest )throws Exception {
        final Channel channel = channelRequest.getChannelContext().channel();
        statisticService.onChannelRequest(channelRequest);
        ServerLogger.logger.debug(String.format("Session %s. Opened",channel));
        final RequestSession requestSession = wrapped?new RequestSessionActorWrapper(
                requestSessionFactory.getRequestSession(channelRequest, actorService.getActorSystem())
                    , actorService.getActorSystem()
                    , ratio
            ):requestSessionFactory.getRequestSession(channelRequest, actorService.getActorSystem())
        ;

        onSessionOpen(channel.id().asShortText(), requestSession);
        requestSession.onOpen();
        channel.closeFuture().addListener(future -> {
            requestSession.onClose();
            ServerLogger.logger.debug(String.format("Session %s, lifecycle %s ms. Closed",channelRequest.getChannelContext().channel()
                    ,(System.currentTimeMillis()-channelRequest.getTimestamp())));
            onSessionClose(channel.id().asShortText());
        });
        return requestSession;
    }


}
