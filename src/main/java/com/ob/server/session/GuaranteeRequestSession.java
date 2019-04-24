package com.ob.server.session;

import com.ob.server.ChannelRequestDto;
import com.ob.server.SocketFrameWrapper;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GuaranteeRequestSession <OUT> implements RequestSession {
    private Logger logger = LoggerFactory.getLogger(GuaranteeRequestSession.class);

    protected ChannelRequestDto channelRequestDto;
    protected String sessionId;
    protected SocketFrameWrapper<Object, OUT> socketFrameWrapper;

    protected AtomicBoolean writing = new AtomicBoolean();
    protected Queue messages = new ConcurrentLinkedQueue();

    public GuaranteeRequestSession(ChannelRequestDto channelRequestDto,
                                   SocketFrameWrapper<Object, OUT> socketFrameWrapper) {
        this.channelRequestDto = channelRequestDto;
        this.socketFrameWrapper = socketFrameWrapper;
        this.sessionId = channelRequestDto.getChannelContext().channel().id().asShortText();
    }

    private boolean send(ChannelHandlerContext channelHandlerContext){
        boolean successful = true;
        Object message;
        while ((message = messages.peek()) != null &&
                (successful = channelHandlerContext.channel().isWritable())) {
            channelHandlerContext
                    .channel()
                    .writeAndFlush(socketFrameWrapper
                                    .wrap(message)
                            , channelHandlerContext.voidPromise());
            messages.poll();
        }
        return successful;
    }

    public void trySend(Object message) {
        if (message != null) {
            try {
                ChannelHandlerContext channelHandlerContext
                        = channelRequestDto.getChannelContext();
                messages.offer(message);
                if(!writing.getAndSet(true) && channelHandlerContext.channel().isOpen()){
                    try {
                        while (!send(channelHandlerContext)) {
                            Thread.sleep(1);
                        }
                    }finally {
                        writing.getAndSet(false);
                    }
                }
            } catch (Exception e) {
                logger.error("Writing into socket error. SessionId - {}, message - {}", sessionId, message, e);
            }
        }
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public ChannelRequestDto getChannelRequest() {
        return channelRequestDto;
    }
}
