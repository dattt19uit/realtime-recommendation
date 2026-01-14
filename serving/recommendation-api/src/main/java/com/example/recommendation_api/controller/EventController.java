package com.example.recommendation_api.controller;

import com.example.recommendation_api.dto.UserEventRequest;
import com.example.recommendation_api.service.EventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/event")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventProducer producer;
    private final ObjectMapper mapper = new ObjectMapper();

    public EventController(EventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<?> ingest(@RequestBody UserEventRequest req) throws Exception {
        Map<String, Object> event = new HashMap<>();
        event.put("user_id", req.getUserId());
        event.put("item_id", req.getItemId());
        event.put("event_type", req.getEventType());
        event.put("event_time", System.currentTimeMillis());

        producer.sendEvent(mapper.writeValueAsString(event));
        System.out.println(mapper.writeValueAsString(event));
        return ResponseEntity.ok().build();
    }
}

