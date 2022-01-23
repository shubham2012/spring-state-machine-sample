package com.statemachine.sample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/heartbeat"})
@CrossOrigin
public class HeartBeatController {
    public HeartBeatController() {
    }

    @GetMapping({""})
    public ResponseEntity checkHeartBeat() {
        return ResponseEntity.ok("Alive");
    }
}
