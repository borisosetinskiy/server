package com.ob.server;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.io.File;
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
   private String wsPath;
   private boolean cors;

   public SslContext getSslCtx() {
      return sslCtx;
   }


   public void init() {
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
      System.out.println("Key file:"+key);
      if(ssl) {
         this.keyFile = new File(key);
         Assert.isTrue(keyFile.exists());
      }
   }

   public String getWsPath() {
      return wsPath;
   }

   public void setWsPath(String wsPath) {
      this.wsPath = wsPath;
   }

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
              ", wsPath=" + wsPath +
              ", cors=" + cors +
              '}';
   }
}
