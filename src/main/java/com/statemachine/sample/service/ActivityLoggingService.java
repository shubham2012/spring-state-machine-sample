package com.statemachine.sample.service;

import com.statemachine.sample.domain.ActivityEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ActivityLoggingService {

    /**
     * Simply save the activity
     * @param entry
     * @return
     */
    public ActivityEntry addActivity(ActivityEntry entry){
        //save activity
        log.info("Saved activity: {}", entry);
        return entry;
    }

}
