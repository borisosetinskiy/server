package com.ob.server.session;

import com.ob.common.akka.WithEventService;
import com.ob.server.ServerLogger;
import com.ob.server.resolvers.ChannelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;


/**
 * Created by boris on 11.04.2016.
 */
public class DefaultRequestService extends WithEventService implements RequestService {

    private RequestSessionFactory requestSessionFactory;
    private StatisticService statisticService;
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
        final RequestSession requestSession = requestSessionFactory.newRequestSession(channelRequest);
        statisticService.onChannelRequest(channelRequest);
        onSessionOpen(channelRequest.getChannelContext().channel().id().asShortText(), requestSession);
        requestSession.onOpen();
        ServerLogger.logger.debug(String.format("Session %s. Opened",channelRequest.getChannelContext().channel().id().asShortText()));
        channelRequest.getChannelContext().channel().closeFuture().addListener(future -> {
            onSessionClose(channelRequest.getChannelContext().channel().id().asShortText());
            requestSession.onClose();
            ServerLogger.logger.debug(String.format("Session %s, lifecycle %s ms. Closed"
                    , channelRequest.getChannelContext().channel().id().asShortText()
                    ,(System.currentTimeMillis()-channelRequest.getTimestamp())));

        });
        return requestSession;
    }


}
