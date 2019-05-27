package com.ob.server;

import com.ob.server.session.RequestSession;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;


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
//                try{
//                    Thread.sleep(1000*5);
//                }catch (Exception e){}
                var1.getChannelContext()
                        .writeAndFlush(
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                                , HttpResponseStatus.OK
                                , Unpooled.copiedBuffer(String.valueOf(System.currentTimeMillis())
                                , CharsetUtil.UTF_8))).addListener(ChannelFutureListener.CLOSE);


//                Executors.newSingleThreadExecutor().execute(() -> {
//                    while(!Thread.currentThread().isInterrupted()){
//                        var1.getChannelContext().channel().writeAndFlush(new TextWebSocketFrame(String.valueOf(System.currentTimeMillis())));
//                        try{
//                            Thread.sleep(100);
//                        }catch (Exception e){}
//                    }
//                });
            }
        }).setHttp().setBossNumber(1).withAgentHandler().start();
        System.in.read();
        nettyServer.stop();
    }
}
