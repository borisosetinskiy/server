package com.ob.server.session;

import com.ob.common.thread.TFactory;
import com.ob.server.resolvers.ChannelRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by boris on 3/28/2017.
 */
public class StatisticServiceImpl implements StatisticService {
    private  final static Logger logger = LoggerFactory.getLogger("LifeSessions");
    private final long scheduledTime;
    private final Map<String, ChannelRequest> channels = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new TFactory());

    public StatisticServiceImpl(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @PostConstruct
    public void init(){
        scheduler.scheduleWithFixedDelay(() -> statistics(), scheduledTime, scheduledTime, TimeUnit.SECONDS);
    }
    @PreDestroy
    public void destroy(){
        if(!scheduler.isTerminated()){
            scheduler.shutdown();
        }
    }

    @Override
    public void onChannelRequest(ChannelRequest channelRequest) {
        channels.put(channelRequest.getChannelContext().channel().id().asShortText(), channelRequest);
        channelRequest.getChannelContext().channel().closeFuture().addListener(future -> {
            channels.remove(channelRequest.getChannelContext().channel().id().asShortText());
        });
    }
    public void statistics(){
        logger.debug(String.format("Life %s", channels.size()));
//        long now = System.currentTimeMillis();
//        channels.values().forEach(channel -> {});
    }
}
