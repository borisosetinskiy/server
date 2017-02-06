package com.ob.common.akka;

import akka.actor.ActorRef;

/**
 * Created by boris on 19.04.2016.
 */
public interface CreateActor {
    ActorRef actor(String name);
    void startUp();
    void shutDown();
}
