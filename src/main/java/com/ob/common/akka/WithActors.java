package com.ob.common.akka;

import akka.actor.ActorRef;
import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Map;

/**
 * Created by boris on 13.04.2016.
 */
public abstract class WithActors<T> extends WithActorService implements CreateActor{
    protected Map<T, ActorRef> actors = Maps.newHashMap();


    public Collection<ActorRef> actors(){
        return actors.values();
    }

    public Collection<T> actorsNames(){
        return actors.keySet();
    }

    public ActorRef getActor(T key){
        return actors.get(key);
    }
    protected abstract Collection<T> collection();
    protected abstract String toString(T value);

    @PostConstruct
    public void start(){
        for(T key : collection()){
            addActor(key);
        }
        startUp();

    }
    public void addActor(T key){
        actors.put(key, actor(toString(key)));
    }
    @PreDestroy
    public void stop(){
        shutDown();
        actors.values().forEach(ActorUtil::gracefulReadyStop);

    }
    public void startUp(){}
    public void shutDown(){}
}