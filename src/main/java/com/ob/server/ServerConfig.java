/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslContextBuilder
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.util.Assert
 */
package com.ob.server;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.NettyRuntime;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


final class ServerConfig {
    private SslContext sslCtx;
    private File certFile;
    private File keyFile;
    private int bossNumber = 1;
    private int workNumber = NettyRuntime.availableProcessors() * 3;
    public SslContext getSslCtx() {
        return this.sslCtx;
    }

    public boolean isSsl(){
        return sslCtx!=null;
    }
    public void setCertificate(String key, String cert) {
        Path keyPath = Paths.get(key);
        Path certPath = Paths.get(cert);
        if(cert == null || key == null
                || !Files.exists(keyPath)
                || !Files.exists(certPath)
        ) throw new RuntimeException();

        this.certFile = certPath.toFile();
        this.keyFile = keyPath.toFile();
        try {
            this.sslCtx = SslContextBuilder.forServer(this.certFile, this.keyFile).build();
        }
        catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }


    public int getBossNumber() {
        return bossNumber;
    }

    public void setBossNumber(int bossNumber) {
        this.bossNumber = bossNumber;
    }

    public int getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(Integer workNumber) {
        if(workNumber != null)
            this.workNumber = workNumber;
    }

}

