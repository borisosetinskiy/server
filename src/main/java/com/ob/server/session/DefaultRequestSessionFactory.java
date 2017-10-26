package com.ob.server.session;

import com.ob.common.akka.WithEventService;
import com.ob.event.EventLogic;
import com.ob.event.EventLogicFactory;
import com.ob.server.resolvers.ChannelRequest;
import org.springframework.beans.factory.annotation.Required;

public class DefaultRequestSessionFactory extends WithEventService implements RequestSessionFactory{
    private RequestSessionFactory requestSessionFactory;
    @Required
    public void setRequestSessionFactory(RequestSessionFactory requestSessionFactory) {
        this.requestSessionFactory = requestSessionFactory;
    }
    @Override
    public RequestSession newRequestSession(ChannelRequest channelRequest) {
        final RequestSession requestSession = requestSessionFactory.newRequestSession(channelRequest);
        if(requestSession instanceof AbstractRequestSession){
            eventService.create(((AbstractRequestSession) requestSession).name(), requestSession.getSessionId(), new EventLogicFactory() {
                @Override
                public EventLogic create() {
                    return (AbstractRequestSession)requestSession;
                }
            });
        }
        return requestSession;
    }
}
