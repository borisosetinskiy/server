package com.ob.server.http.handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.ArrayList;

public class HttpChannelHandlerFactory implements ChannelHandlerFactory {
    private boolean isWithCompressor;
    private boolean isWithChunkedWrite;
    @Override
    public ChannelHandler[] create() {
        ArrayList<ChannelHandler> arrayList = new ArrayList();
        int i = 0;
        if(isWithCompressor) {
            arrayList.add(new HttpContentCompressor());
            ++i;
        }
        if(isWithChunkedWrite) {
            arrayList.add(new ChunkedWriteHandler());
            ++i;
        }
        return arrayList.toArray(new ChannelHandler[i]);
    }
    public void setWithCompressor(boolean withCompressor) {
        isWithCompressor = withCompressor;
    }
    public void setWithChunkedWrite(boolean withChunkedWrite) {
        isWithChunkedWrite = withChunkedWrite;
    }
}
