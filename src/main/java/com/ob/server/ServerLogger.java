package com.ob.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by boris on 20.04.2016.
 */
public class ServerLogger {
    public static final Logger logger = LoggerFactory.getLogger(ServerLogger.class);
    public static final Logger agentLogger = LoggerFactory.getLogger("com.ob.server.agent");
}
