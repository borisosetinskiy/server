package com.ob.server.netty;


import com.ob.server.InitializerFactory;
import com.ob.server.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Required;
/**
 * Created by boris on 19.04.2016.
 */
public class NettyServer {
   private static final int KB = 1024;
   private static final int WRITE_BUFFER_HIGH_WATER_MARK = 256 * KB;
   private static final int WRITE_BUFFER_LOW_WATER_MARK = 64 * KB;
   private static final int RECEIVE_BUFFER_SIZE = 256 * KB;
   private static final int SEND_BUFFER_SIZE = 256 * KB;
   //ChannelMatchers

   private final ServerConfig config;

   private InitializerFactory initializerFactory;
   private final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
   private EventLoopGroup bossGroup;
   private EventLoopGroup workerGroup;
   private ChannelFuture future;

   public void setServerShutdown(ServerShutdown serverShutdown) {
      this.serverShutdown = serverShutdown;
   }

   private ServerShutdown serverShutdown;

   public NettyServer(ServerConfig config) {
      this.config = config;
      bossGroup = ServerConfig.getBossGroup();
      workerGroup = ServerConfig.getWorkerGroup();

   }

   @Required
   public void setInitializerFactory(InitializerFactory initializerFactory) {
      this.initializerFactory = initializerFactory;
   }

//   @PostConstruct
   public void startUp() throws Exception {

      ServerBootstrap bootstrap = new ServerBootstrap();
      if(serverShutdown!=null){
         serverShutdown.setChannelGroup(allChannels);
      }

      bootstrap.group(bossGroup, workerGroup);
      if(config.isEpoll()) {
         bootstrap.channel(EpollServerSocketChannel.class);
      }else  {
         bootstrap.channel(NioServerSocketChannel.class);
      }

      bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
      bootstrap.handler(new LoggingHandler(LogLevel.WARN))
              .childHandler(initializerFactory.createInitializer(config, allChannels))
              .option(ChannelOption.SO_REUSEADDR, true)
              .option(ChannelOption.SO_BACKLOG, KB)
              .childOption(ChannelOption.SO_RCVBUF, RECEIVE_BUFFER_SIZE)
              .childOption(ChannelOption.SO_SNDBUF, SEND_BUFFER_SIZE)
              .childOption(ChannelOption.TCP_NODELAY, true)
              .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(WRITE_BUFFER_LOW_WATER_MARK, WRITE_BUFFER_HIGH_WATER_MARK))
              .childOption(ChannelOption.SO_KEEPALIVE, true);


      future = bootstrap.bind(config.getPort());
      allChannels.add(future.channel());
   }
//   @PreDestroy
   public void shutDown() throws Exception {
      if(serverShutdown != null){
         serverShutdown.shutDown();
      }
      try {
         ChannelGroupFuture future = allChannels.close();
         future.awaitUninterruptibly();
      }catch (Exception e){}
      try {
         if(!bossGroup.isShutdown())
            bossGroup.shutdownGracefully();
      }catch (Exception e){}
      try {
         if(!workerGroup.isShutdown())
            workerGroup.shutdownGracefully();
      }catch (Exception e){}
   }

   @Override
   public String toString() {
      return "NettyServer{" +
              "config=" + config +
              '}';
   }
}
