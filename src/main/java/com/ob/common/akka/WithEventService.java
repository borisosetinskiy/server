package com.ob.common.akka;

import com.ob.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by boris on 1/29/2017.
 */
public class WithEventService {
    protected EventService eventService;

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
