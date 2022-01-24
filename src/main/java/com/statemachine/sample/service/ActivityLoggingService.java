package com.statemachine.sample.service;

import com.statemachine.sample.constants.EventStatus;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.domain.ActivityEntry;
import org.springframework.stereotype.Service;

@Service
public class ActivityLoggingService {

    public ActivityEntry addActivity(ActivityEntry entry){
        //save activity
        return entry;
    }

    public ActivityEntry addActivity(String orderId, OrderStatus from, OrderService to, OrderUpdateEvent event, EventStatus eventStatus, String remarks){
       ActivityEntry entry = new ActivityEntry();
       entry.setRemarks(remarks);
       entry.setOrderId(orderId);
       entry.setEvent(event);
       entry.setEventStatus(eventStatus);
       entry.setFromStatus(from);
       entry.setToStatus(to);
       //save activity
       return entry;
    }
}
