package com.ob.server.session;

import com.ob.server.resolvers.ChannelRequest;

/**
 * Created by boris on 3/28/2017.
 */
public interface StatisticService {
    void onChannelRequest(ChannelRequest channel);
    StatisticService STATISTIC_SERVICE = channel -> {

    };
}
