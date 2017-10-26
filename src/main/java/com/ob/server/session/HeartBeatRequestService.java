package com.ob.server.session;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by boris on 19.05.2016.
 */
public class HeartBeatRequestService extends DefaultRequestService {
    private HeartBeatService heartBeatService;

    @Autowired
    public void setHeartBeatService(HeartBeatService heartBeatService) {
        this.heartBeatService = heartBeatService;
    }

    protected void onSessionOpen(String key, RequestSession requestSession ){
        heartBeatService.addSession(key, requestSession);
    }

    protected void onSessionClose(String key){
        heartBeatService.removeSession(key);
    }
}
