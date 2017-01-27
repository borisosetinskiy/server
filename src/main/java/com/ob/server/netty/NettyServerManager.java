package com.ob.server.netty;

import com.google.common.collect.Sets;
import com.ob.server.ServerLogger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;

/**
 * Created by boris on 19.04.2016.
 */
public class NettyServerManager {
    Set<NettyServer> servers = Sets.newHashSet();

    @Required
    public void addServer(NettyServer server){
        servers.add(server);
    }


    public void startUp()throws Exception{
        if(servers.isEmpty()) throw new Exception("Please add at least one server!");
        for(NettyServer server  : servers) {
            server.startUp();
            ServerLogger.logger.debug("Starting..."+server);
        }
    }

    public void shutDown()throws Exception{
        for(NettyServer server  : servers) {
            server.shutDown();
            ServerLogger.logger.debug("Stopping..."+server);
        }
        NettyServer.shutDownStatic();
    }
}
