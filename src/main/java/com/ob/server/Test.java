package com.ob.server;

import com.ob.server.session.HeartBeatFactory;
import com.ob.server.session.RequestSession;
import com.ob.server.session.RequestSessionFactory;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Test {
    public static void main(String[] args) throws Exception {

        NettyServer nettyServer = NettyServer.create(8080, var1 -> new RequestSession() {

            @Override
            public String getSessionId() {
                return var1.getChannelContext().channel().id().asShortText();
            }

            @Override
            public ChannelRequestDto getChannelRequest() {
                return var1;
            }
            @Override
            public void onOpen() {
                try{
                    Thread.sleep(1000*5);
                }catch (Exception e){}
                var1.getChannelContext().channel().writeAndFlush(new TextWebSocketFrame(String.valueOf(System.currentTimeMillis())));

//                Executors.newSingleThreadExecutor().execute(() -> {
//                    while(!Thread.currentThread().isInterrupted()){
//                        var1.getChannelContext().channel().writeAndFlush(new TextWebSocketFrame(String.valueOf(System.currentTimeMillis())));
//                        try{
//                            Thread.sleep(100);
//                        }catch (Exception e){}
//                    }
//                });
            }
        }).setBossNumber(1).withAgentHandler().setHeartBeatFactory(() -> new PingWebSocketFrame()).start();
        System.in.read();
        nettyServer.stop();
    }
}
