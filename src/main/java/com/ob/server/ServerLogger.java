/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ob.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogger {
    public static final Logger logger = LoggerFactory.getLogger(ServerLogger.class);
    public static final Logger agentLogger = LoggerFactory.getLogger("com.ob.server.agent");
    public static final Logger loggerProblem = LoggerFactory.getLogger("com.ob.server.problem");
    public static final Logger loggerChannel = LoggerFactory.getLogger("com.ob.server.channel");
    public static final Logger loggerWebSocket = LoggerFactory.getLogger("com.ob.server.handlers.websocket");
    public static final Logger loggerTrash = LoggerFactory.getLogger("com.ob.server.trash");
    public static final Logger loggerMessage = LoggerFactory.getLogger("com.ob.server.message");
}

