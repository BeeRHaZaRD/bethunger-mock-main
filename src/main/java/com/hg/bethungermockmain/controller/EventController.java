package com.hg.bethungermockmain.controller;

import com.hg.bethungermockmain.dto.PlannedEventRequestDTO;
import com.hg.bethungermockmain.dto.SupplyRequestDTO;
import com.hg.bethungermockmain.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "/planned-event")
    public void runPlannedEvent(@RequestBody @Valid PlannedEventRequestDTO plannedEventRequestDTO) {
        eventService.runPlannedEvent(plannedEventRequestDTO);
    }

    @PostMapping(path = "/supply")
    public void makeSupply(@RequestBody SupplyRequestDTO supplyRequestDTO) {
        eventService.makeSupply(supplyRequestDTO);
    }
}
