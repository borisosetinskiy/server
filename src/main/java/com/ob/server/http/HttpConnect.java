package com.ob.server.http;

import com.ob.server.ServerLogger;
import io.netty.handler.codec.http.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

/**
 * Created by boris on 12/16/2016.
 */
public class HttpConnect {
    public Object2ObjectArrayMap<String, String> context;
    public HttpMethod method;
    public boolean connected;
    public final String channelId;

    public HttpConnect(String channelId) {
        this.channelId = channelId;
    }

    public void process(Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            method = request.method();
            ServerLogger.agentLogger.debug(String.format("Channel %s \nRequest %s"
                    , channelId, PrintUtil.appendRequest(new StringBuilder(256), request)));
            context = QueryDecoder.decode(request.uri());
        }else if(msg instanceof LastHttpContent){
            if(msg instanceof DefaultLastHttpContent && HttpMethod.POST.equals(method))
                com.ob.server.http.HttpUtil.decodeParam((HttpContent)msg, context);
            connected = true;
        }
    }
//    EmptyLastHttpContent   DefaultLastHttpContent

    public static HttpConnect state(Object msg, HttpConnect in, String channelId) {
        HttpConnect machine = in;
        if(machine == null){
            machine = new HttpConnect(channelId);
        }
        machine.process(msg);
        return machine;
    }
}
