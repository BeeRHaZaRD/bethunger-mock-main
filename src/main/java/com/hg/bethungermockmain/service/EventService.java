package com.hg.bethungermockmain.service;

import com.hg.bethungermockmain.dto.*;
import com.hg.bethungermockmain.model.HappenedEventType;
import com.hg.bethungermockmain.model.PlayerEventType;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

@CommonsLog
@Service
public class EventService {
    private final Random random = new Random();
    private Long gameId;
    private List<Long> alivePlayers;
    private ScheduledFuture<?> generateEventsScheduler;

    private final WebClient webClient;
    private final TaskScheduler taskScheduler;

    @Autowired
    public EventService(WebClient webClient, TaskScheduler taskScheduler) {
        this.webClient = webClient;
        this.taskScheduler = taskScheduler;
    }

    public void startGame(Long gameId, GameStartDTO gameStartDTO) {
        this.gameId = gameId;
        this.alivePlayers = gameStartDTO.playersId();

        generateEvents();
    }

    // randomly generates PLAYER and OTHER events
    private void generateEvents() {
        if (this.gameId == null || this.alivePlayers == null) {
            throw new IllegalStateException("init method wasn't called");
        }

        Runnable generateEventsTask = () -> {
            HappenedEventDTO happenedEventDTO;
            // player or other event
            HappenedEventType happenedEventType = random.nextFloat() < 0.8 ? HappenedEventType.PLAYER : HappenedEventType.OTHER;

            if (happenedEventType == HappenedEventType.PLAYER) {
                // player
                int playerIdx = random.nextInt(alivePlayers.size());
                Long playerId = alivePlayers.get(playerIdx);

                // killed or injured
                PlayerEventType playerEventType = random.nextFloat() < 0.7 ? PlayerEventType.KILLED : PlayerEventType.SLIGHT_INJURY;
                if (playerEventType == PlayerEventType.KILLED) {
                    alivePlayers.remove(playerIdx);
                }
                happenedEventDTO = new HPlayerEventDTO(playerEventType, playerId);
                log.debug("Game #%d - player %d is %s".formatted(gameId, playerId, playerEventType));
            } else {
                happenedEventDTO = new HOtherEventDTO(null, "Распорядитель выступил с объявлением");
                log.debug("Game #%d - other".formatted(gameId));
            }

            webClient.post()
                .uri("/games/" + gameId + "/happened-events")
                .bodyValue(happenedEventDTO)
                .retrieve()
                .toBodilessEntity()
                .block();

            if (alivePlayers.size() == 1) {
                log.debug("Game #%d - FINISHED - Winner: player %d".formatted(gameId, alivePlayers.get(0)));
                resetState();
                generateEventsScheduler.cancel(true);
            }
        };
        generateEventsScheduler = taskScheduler.scheduleWithFixedDelay(generateEventsTask, Instant.now().plusSeconds(5), Duration.ofSeconds(5));
    }

    public void runPlannedEvent(PlannedEventRequestDTO plannedEvent) {
        Long gameId = this.gameId;
        if (gameId == null) {
            throw new IllegalStateException("Game has already finished");
        }
        log.debug("Game #%d - Planned event requested".formatted(gameId));
        webClient.post()
            .uri("/games/" + gameId + "/happened-events")
            .bodyValue(new HPlannedEventDTO(plannedEvent.id()))
            .retrieve()
            .toBodilessEntity()
            .delaySubscription(Duration.ofSeconds(15))
            .subscribe();
    }

    private void resetState() {
        this.gameId = null;
        this.alivePlayers = null;
    }
}
