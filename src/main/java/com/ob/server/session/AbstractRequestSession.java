package com.ob.server.session;

import com.ob.common.data.Entry;
import com.ob.event.EventNodeEndPoint;
import com.ob.event.akka.AkkaEventLogic;
import com.ob.server.ServerLogger;
import com.ob.server.resolvers.ChannelRequest;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by boris on 07.04.2016.
 */
public abstract class AbstractRequestSession extends AkkaEventLogic implements RequestSession{
    protected final ChannelRequest channelRequest;
    protected String sessionId;
    protected EntryAggregator entryAggregator;
    protected final ResponseFormatter responseFormatter;
    protected SessionParams sessionParams = SessionParams.EMPTY;
    public AbstractRequestSession(String sessionId, String withDispatcher, String withMailbox, ChannelRequest channelRequest, ResponseFormatter responseFormatter) {
        super(channelRequest.getChannelContext().channel().id().asShortText(),  withDispatcher, withMailbox);
        this.channelRequest = channelRequest;
        this.sessionId = sessionId;
        this.responseFormatter = responseFormatter;
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
            if(message instanceof Entry) {
                if (sessionParams.getThrottlePause() > 0) {
                    entryAggregator.add(message);
                }
            }
            onWrite(responseFormatter.format(message));

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
        initSessionParams();
        if(sessionParams().getThrottlePause()>0){
            entryAggregator = new EntryAggregator(){
                private Map<Object, EntryWithTime> messages = new ConcurrentHashMap();
                @Override
                public void add(Object o) {
                    if(o instanceof Entry) {
                        Entry entry = (Entry)o;
                        EntryWithTime entryWithTime = messages.get(entry.key());
                        if(entryWithTime == null){
                            entryWithTime = new EntryWithTime();
                            messages.put(entry.key(), entryWithTime);
                        }
                        entryWithTime.addEntry(entry);
                    }
                }
                @Override
                public Object[] array() {
                    ArrayList cache = new ArrayList();
                    for(EntryWithTime entryWithTime : messages.values()){
                        Entry entry;
                        if((entry = entryWithTime.isExpired(sessionParams().getThrottlePause()))!=null){
                            cache.add(entry);
                        }
                    };
                    return cache.toArray();
                }
            };
        }
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
    public SessionParams sessionParams() {
        return sessionParams;
    }


    @Override
    public String toString() {
        return sessionId!=null?sessionId:name();
    }

    public abstract void tell(Object o);
    protected abstract void initSessionParams();


}
