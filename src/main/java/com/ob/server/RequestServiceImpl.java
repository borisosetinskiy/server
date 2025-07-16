
package com.ob.server;

import com.ob.server.session.RequestService;
import com.ob.server.session.RequestSession;
import com.ob.server.session.RequestSessionFactory;

public class RequestServiceImpl
        implements RequestService {
    private final RequestSessionFactory requestSessionFactory;


    public RequestServiceImpl(RequestSessionFactory requestSessionFactory) {
        this.requestSessionFactory = requestSessionFactory;
    }

    @Override
    public RequestSession process(ChannelRequestDto channelRequestDto) throws Exception {
        RequestSession requestSession
                = this.requestSessionFactory
                .newRequestSession(channelRequestDto);
        requestSession.onOpen();
        return requestSession;
    }

}
