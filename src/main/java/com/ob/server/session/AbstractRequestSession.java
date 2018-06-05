package com.ob.server.session;

import com.ob.event.EventNodeEndPoint;
import com.ob.event.akka.AkkaEventLogic;
import com.ob.server.ServerLogger;
import com.ob.server.resolvers.ChannelRequest;

/**
 * Created by boris on 07.04.2016.
 */
public abstract class AbstractRequestSession extends AkkaEventLogic implements RequestSession{
    protected final ChannelRequest channelRequest;
    protected String sessionId;


    public AbstractRequestSession(String sessionId, String withDispatcher, String withMailbox, ChannelRequest channelRequest) {
        super(channelRequest.getChannelContext().channel().id().asShortText(),  withDispatcher, withMailbox);
        this.channelRequest = channelRequest;
        this.sessionId = sessionId;
        channelRequest.getChannelContext().channel().closeFuture().addListener(future -> {
            try {
                release();
            }catch (Exception e){}
        });
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {
        onClose();
    }


    @Override
    public void onEvent(Object message, EventNodeEndPoint eventNodeEndPoint) {
        try {
            onWrite(message);
        } catch (Exception e) {
            ServerLogger.loggerWrite.error(String.format("Session %s, error %s, message %s", getSessionId(), e.getMessage(), message));
        }
    }

    @Override
    public void onWrite(Object message) {

    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onRead(Object message) {

    }

    @Override
    public ChannelRequest getChannelRequest() {
        return channelRequest;
    }



    @Override
    public String toString() {
        return sessionId!=null?sessionId:name();
    }

    public abstract void tell(Object o);


    @Override
    public void tellSync(Object o) {

    }

}
