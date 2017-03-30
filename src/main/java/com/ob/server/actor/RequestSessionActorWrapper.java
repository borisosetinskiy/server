package com.ob.server.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.ob.common.akka.ActorUtil;
import com.ob.common.data.Key;
import com.ob.server.MessageAggregator;
import com.ob.server.http.FlashResponseMessage;
import com.ob.server.http.RequestSession;
import com.ob.server.resolvers.ChannelRequest;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by boris on 1/3/2017.
 */
public class RequestSessionActorWrapper implements RequestSession {
    private final RequestSession requestSession;

    private ActorRef requestActor;
    private MessageAggregator messageAggregator;

    private AtomicBoolean lock = new AtomicBoolean();

    public RequestSessionActorWrapper(RequestSession requestSession, ActorSystem actorSystem, int ratio) {
        this.requestSession = requestSession;
        requestSession.setSelf(this);
        if(ratio < 1)
            throw new IllegalArgumentException("Ratio can't be less 1");

        if(requestSession.getThrottlePause() == 0){
            messageAggregator = new ConcurrentQueueMessageAggregator(requestSession.getBufferSize(), ratio);
        }else{
            messageAggregator = new MessageAggregator(){
                private Map currentElements =
                        new ConcurrentHashMap();
                private Object2ObjectArrayMap<Object, AtomicLong> timestampCollector = new Object2ObjectArrayMap<>();
                private Queue cache = new LinkedList();
                @Override
                public void add(Object o) {
                    if(o instanceof Key) {
                        Key key = (Key)o;
                        final long now = System.currentTimeMillis();
                        currentElements.put(key.key(), o);
                        AtomicLong time = timestampCollector.get(key.key());
                        if(time == null ){
                            time = new AtomicLong(now);
                            timestampCollector.put(key.key(), time);
                        }else if(time.get() == -1){
                            time.set(now);
                        }
                    }
                }

                @Override
                public Object poll() {
                    return cache.poll();
                }

                @Override
                public MessageAggregator flash() {
                    final long now = System.currentTimeMillis();
                    timestampCollector.forEach((key,time)->{
                        if( time.get() > 0 && now - time.get() >= getThrottlePause()){
                            time.set(-1);
                            cache.add(currentElements.get(key));
                        }
                    });
                    return this;
                }

            };
        }

        try{
            requestActor = actorSystem.actorOf(RequestActor.props(requestSession, messageAggregator, lock).withMailbox("akka.actor.response-mailbox")
                    , "MessageReceiver-" + requestSession.getName());
        }catch (Exception e){
            requestActor = actorSystem.actorOf(RequestActor.props(requestSession, messageAggregator, lock)
                    , "MessageReceiver-" + requestSession.getName());
        }


        requestSession.getChannelRequest().getChannelContext().channel().closeFuture().addListener(future -> {
            ActorUtil.gracefulReadyStop(requestActor);
        });

    }

    @Override
    public void onWrite(Object message) {
        if(message instanceof FlashResponseMessage){
            requestSession.onWrite(message);
        }{
            messageAggregator.add(message);
            if( !lock.getAndSet(true) &&
                    !requestActor.isTerminated()){
                requestActor.tell(RequestActor.Received.i, requestActor);
            }
        }
    }

    @Override
    public String getName() {
        return requestSession.getName();
    }

    @Override
    public void onOpen() {
        requestSession.onOpen();
    }

    @Override
    public void onClose() {
        requestSession.onClose();
    }

    @Override
    public void onRead(Object message) {
        requestSession.onRead(message);
    }

    @Override
    public ChannelRequest getChannelRequest() {
        return requestSession.getChannelRequest();
    }

    @Override
    public Object getType() {
        return requestSession.getType();
    }

    @Override
    public void setSelf(RequestSession self) {
        requestSession.setSelf(self);
    }

    @Override
    public int getThrottlePause() {
        return requestSession.getThrottlePause();
    }

    @Override
    public int getBufferSize() {
        return requestSession.getBufferSize();
    }

    @Override
    public RequestSession unwrap() {
        return requestSession;
    }


    @Override
    public String toString() {
        return requestSession!=null?requestSession.getName():super.toString();
    }
}
