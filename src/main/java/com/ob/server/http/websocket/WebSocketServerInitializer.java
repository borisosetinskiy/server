/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ob.server.http.websocket;


import com.ob.server.ServerConfig;
import com.ob.server.resolvers.ResponderResolver;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;


/**
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    final ServerConfig config;
    final ResponderResolver responderResolver;
    final ChannelGroup allChannels;
    final AccessHandler accessHandler;

    public WebSocketServerInitializer(ServerConfig config, ResponderResolver responderResolver, ChannelGroup allChannels, AccessHandler accessHandler) {
        this.config = config;
        this.responderResolver = responderResolver;
        this.allChannels = allChannels;
        this.accessHandler = accessHandler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(config.isSsl()){
            pipeline.addLast(config.getSslCtx().newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new DefaultWebSocketServerHandler(config.getWsPath(), responderResolver, allChannels, accessHandler));
        pipeline.addLast(new TextWebSocketServerHandler());
    }
}
