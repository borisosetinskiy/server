package com.ob.server.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.ob.server.MessageAggregator;
import com.ob.server.ServerLogger;
import com.ob.server.http.RequestSession;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by boris on 07.04.2016.
 */
public class RequestActor extends UntypedActor {
    private final RequestSession session;
    private final MessageAggregator messages;
    private final AtomicBoolean lock;


    public RequestActor(final RequestSession session, MessageAggregator messages, AtomicBoolean lock) {
        this.session = session;
        this.messages = messages;
        this.lock = lock;
        session.getChannelRequest().getChannelContext().channel().closeFuture().addListener(future -> {
            context().stop(self());
        });
    }

    public static Props props(RequestSession session, MessageAggregator messages, AtomicBoolean lock) {
            return Props.create(new Creator<RequestActor>() {
            private static final long serialVersionUID = 1L;
            @Override
            public RequestActor create() throws Exception {
                return new RequestActor(session, messages, lock);
            }
        });
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        try {
            if(message instanceof Received) {
                try{
                    session.onWrite(messages.flash());
                }finally {
                    lock.set(false);
                }
            }
        } catch (Exception e) {
            ServerLogger.logger.debug(e.toString());
        }
    }

    @Override
    public void preStart()throws Exception{

    }

    @Override
    public void postStop()throws Exception{
        session.onClose();
        ServerLogger.logger.debug("Session:"+session.getName()+" is stopping...");
    }


    public static final class Received{
        public static final Received i = new Received();
    }

}
