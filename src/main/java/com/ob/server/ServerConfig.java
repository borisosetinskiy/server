package com.ob.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by boris on 19.04.2016.
 */
public final class ServerConfig {
   public int port;
   private int allTimeoutSeconds = 300;
   private int writeTimeoutSeconds = 300;
   private boolean ssl;
   private SslContext sslCtx;
   private File certFile;
   private File keyFile;
   private boolean cors;

   private static boolean epoll;
   private static AtomicBoolean init = new AtomicBoolean();
   private static EventLoopGroup bossGroup;
   private static EventLoopGroup workerGroup;


   public static boolean isEpoll() {
      return epoll;
   }

   public SslContext getSslCtx() {
      return sslCtx;
   }


   public static void init(boolean e){
      epoll = e;
      if(epoll) {
         bossGroup = new EpollEventLoopGroup(1);
         workerGroup = new EpollEventLoopGroup();
      }else{
         bossGroup = new NioEventLoopGroup(1);
         workerGroup = new NioEventLoopGroup();
      }
      init.set(true);
   }

   public static EventLoopGroup getBossGroup() {
      return bossGroup;
   }

   public static EventLoopGroup getWorkerGroup() {
      return workerGroup;
   }

   public void init() {
      if(!init.get())
         throw new RuntimeException("Static context not initialized!!!");
      if(ssl){
         Assert.notNull(certFile);
         Assert.notNull(keyFile);
         try {
            sslCtx = SslContextBuilder.forServer(certFile, keyFile)
                    .build();
         }catch(Exception e){
            throw new RuntimeException(e);
         }
      }
   }

   public void setCertFile(String cert) {
      System.out.println("Cert file:"+cert);
      if(ssl){
         this.certFile = new File(cert);
         Assert.isTrue(certFile.exists());
      }
   }

   public void setKeyFile(String key) {
      System.out.println("Entry file:"+key);
      if(ssl) {
         this.keyFile = new File(key);
         Assert.isTrue(keyFile.exists());
      }
   }

//   public boolean isWithCompressor() {
//      return withCompressor;
//   }
//
//   public void setWithCompressor(boolean withCompressor) {
//      this.withCompressor = withCompressor;
//   }
//
//   public boolean isWithAggregator() {
//      return withAggregator;
//   }
//
//   public void setWithAggregator(boolean withAggregator) {
//      this.withAggregator = withAggregator;
//   }
//
//   public boolean isWithIdle() {
//      return withIdle;
//   }
//
//   public void setWithIdle(boolean withIdle) {
//      this.withIdle = withIdle;
//   }

   public int getPort() {
      return port;
   }

   @Required
   public void setPort(int port) {
      this.port = port;
   }

   public int getAllTimeoutSeconds() {
      return allTimeoutSeconds;
   }

   @Required
   public void setAllTimeoutSeconds(int allTimeoutSeconds) {
      this.allTimeoutSeconds = allTimeoutSeconds;
   }

   public int getWriteTimeoutSeconds() {
      return writeTimeoutSeconds;
   }

   @Required
   public void setWriteTimeoutSeconds(int writeTimeoutSeconds) {
      this.writeTimeoutSeconds = writeTimeoutSeconds;
   }



   public boolean isSsl() {
      return ssl;
   }

   @Required
   public void setSsl(boolean ssl) {
      this.ssl = ssl;
   }

   public boolean isCors() {
      return cors;
   }

   public void setCors(boolean cors) {
      this.cors = cors;
   }

   @Override
   public String toString() {
      return "ServerConfig{" +
              "port=" + port +
              ", allTimeoutSeconds=" + allTimeoutSeconds +
              ", writeTimeoutSeconds=" + writeTimeoutSeconds +
              ", ssl=" + ssl +
              ", sslCtx=" + sslCtx +
              ", certFile=" + certFile +
              ", keyFile=" + keyFile +
              ", cors=" + cors +
//              ", withCompressor=" + withCompressor +
//              ", withAggregator=" + withAggregator +
//              ", withIdle=" + withIdle +
              '}';
   }
}
