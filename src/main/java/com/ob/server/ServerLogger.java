package com.ob.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by boris on 20.04.2016.
 */
public class ServerLogger {
    public static final Logger logger = LoggerFactory.getLogger(ServerLogger.class);
    public static final Logger agentLogger = LoggerFactory.getLogger("com.ob.server.agent");
    public static final Logger loggerProblem = LoggerFactory.getLogger("com.ob.server.problem");
    public static final Logger loggerChannel = LoggerFactory.getLogger("com.ob.server.channel");
    public static final Logger loggerWebSocket = LoggerFactory.getLogger("com.ob.server.websocket");
    public static final Logger loggerTrash = LoggerFactory.getLogger("com.ob.server.trash");
    public static final Logger loggerWrite = LoggerFactory.getLogger("com.ob.server.write");
    public static final Logger loggerMessage = LoggerFactory.getLogger("com.ob.server.message");
}
