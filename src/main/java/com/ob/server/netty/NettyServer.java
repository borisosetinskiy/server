package com.ob.server.netty;


import com.ob.server.InitializerFactory;
import com.ob.server.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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

   private final ServerConfig config;

   private InitializerFactory initializerFactory;
   private static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
   private EventLoopGroup bossGroup;
   private static final EventLoopGroup workerGroup= new NioEventLoopGroup();
   private ChannelFuture future;
   public NettyServer(ServerConfig config) {
      this.config = config;
   }



   @Required
   public void setInitializerFactory(InitializerFactory initializerFactory) {
      this.initializerFactory = initializerFactory;
   }

//   @PostConstruct
   public void startUp() throws Exception {

      bossGroup = new NioEventLoopGroup(1);
      ServerBootstrap bootstrap = new ServerBootstrap();

      bootstrap.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .handler(new LoggingHandler(LogLevel.DEBUG))
              .childHandler(initializerFactory.createInitializer(config, allChannels))
              .option(ChannelOption.SO_REUSEADDR, true)
              .option(ChannelOption.SO_BACKLOG, KB)
              .childOption(ChannelOption.SO_RCVBUF, RECEIVE_BUFFER_SIZE)
              .childOption(ChannelOption.SO_SNDBUF, KB * KB)
              .childOption(ChannelOption.TCP_NODELAY, true)
              .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(WRITE_BUFFER_LOW_WATER_MARK, WRITE_BUFFER_HIGH_WATER_MARK))
              .childOption(ChannelOption.SO_KEEPALIVE, true);

      try{
         future = bootstrap.bind(config.getPort());

      }catch(Exception e){
         throw e;
      }
      allChannels.add(future.channel());
   }
//   @PreDestroy
   public void shutDown() throws Exception {
      bossGroup.shutdownGracefully();
   }

   public static void shutDownStatic(){
      ChannelGroupFuture future = allChannels.close();
      future.awaitUninterruptibly();
      workerGroup.shutdownGracefully();
   }

   @Override
   public String toString() {
      return "NettyServer{" +
              "config=" + config +
              '}';
   }
}
