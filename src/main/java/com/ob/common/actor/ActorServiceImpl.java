package com.ob.common.actor;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

import javax.annotation.PreDestroy;


public class ActorServiceImpl implements ActorService {
	private ActorSystem system;
	public ActorServiceImpl(){
		system = ActorSystem.create("MarketDataSystem", ConfigFactory.load(("application")));
	}
	@Override
	public ActorSystem getActorSystem() {
		return system;
	}
	@PreDestroy
	public void shutdown(){
		system.terminate();
	}
}
