package com.ob.common.akka;

import akka.actor.ActorRef;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boris on 19.04.2016.
 */
public abstract class WithRouter extends WithActorService implements CreateActor {
    protected List<ActorRef> actors = new ArrayList<ActorRef>();
    protected Router router;
    private final int numberOfRoutees;
    private final String name;

    public WithRouter(int numberOfRoutees, String name) {
        this.numberOfRoutees = numberOfRoutees;
        this.name = name;
    }

    @PostConstruct
    public void start(){
        final List<Routee> routees = new ArrayList<Routee>();
        for(int i = 1; i <= numberOfRoutees; ++i){
            final ActorRef actor = actor(name+i);
            actors.add(actor);
            routees.add(new ActorRefRoutee(actor));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
        startUp();

    }
    @PreDestroy
    public void stop(){

        shutDown();
        actors.forEach((actor)->{
            router.removeRoutee(actor);
            ActorUtil.gracefulReadyStop(actor);
        });

    }


}
