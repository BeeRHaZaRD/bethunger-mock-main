package com.hg.bethungermockmain.controller;

import com.hg.bethungermockmain.dto.GameStartDTO;
import com.hg.bethungermockmain.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    private final EventService eventService;

    @Autowired
    public GameController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping(path = "/start-game/{gameId}")
    public void startGame(@PathVariable Long gameId, @RequestBody @Valid GameStartDTO gameStartDTO) {
        eventService.startGame(gameId, gameStartDTO);
    }
}
