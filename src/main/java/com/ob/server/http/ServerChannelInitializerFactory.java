package com.ob.server.http;


import com.ob.server.InitializerFactory;
import com.ob.server.ServerConfig;
import com.ob.server.http.websocket.WebSocketServerInitializer;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by boris on 08.04.2016.
 */
public class ServerChannelInitializerFactory implements InitializerFactory {
    private ResponderResolver resolver;

    @Required
    public void setResolver(ResponderResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public ChannelInitializer createInitializer(ServerConfig config, ChannelGroup allChannels) {
        if(config.getWsPath()!=null){
            return new WebSocketServerInitializer(config, resolver, allChannels);
        }else {
            return new HttpChannelInitializer(config, resolver, allChannels);
        }
    }

    @Override
    public String toString() {
        return "ServerChannelInitializerFactory{" +
                "resolver=" + resolver +
                '}';
    }
}
