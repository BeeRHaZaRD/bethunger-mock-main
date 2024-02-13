package com.hg.bethungermockmain.service;

import com.hg.bethungermockmain.dto.*;
import com.hg.bethungermockmain.model.HappenedEventType;
import com.hg.bethungermockmain.model.PlayerEventType;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

@CommonsLog
@Service
public class EventService {
    private final Random random = ThreadLocalRandom.current();
    private Long gameId;
    private List<Long> alivePlayers;
    private ScheduledFuture<?> generateEventsScheduler;

    private final WebClient webClient;
    private final TaskScheduler taskScheduler;

    @Value("${planned_event_delay}")
    private Duration plannedEventDelay;

    @Value("${supply_delay}")
    private Duration supplyDelay;

    @Value("${event_gen_delay}")
    private Duration eventGenDelay;

    @Value("${event_gen_interval}")
    private Duration eventGenInterval;

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
        generateEventsScheduler = taskScheduler.scheduleWithFixedDelay(generateEventsTask, Instant.now().plus(eventGenDelay), eventGenInterval);
        log.debug("Game #%d - STARTED".formatted(gameId));
    }

    public void runPlannedEvent(PlannedEventRequestDTO plannedEvent) {
        Long gameId = this.gameId;
        if (gameId == null) {
            throw new IllegalStateException("Game has already finished");
        }
        log.debug("Game #%d - Planned event requested".formatted(gameId));

        var body = new HPlannedEventDTO(plannedEvent.id());
        webClient.post()
            .uri("/games/" + gameId + "/happened-events")
            .bodyValue(body)
            .retrieve()
            .toBodilessEntity()
            .delaySubscription(plannedEventDelay)
            .subscribe();
    }

    public void makeSupply(SupplyRequestDTO supplyRequest) {
        Long gameId = this.gameId;
        if (gameId == null) {
            throw new IllegalStateException("Game has already finished");
        }
        if (!alivePlayers.contains(supplyRequest.playerId())) {
            throw new IllegalStateException("Player is already dead");
        }
        log.debug("Game #%d - Supply requested".formatted(gameId));

        var body = new HSupplyEventDTO(supplyRequest.id());
        webClient.post()
            .uri("/games/" + gameId + "/happened-events")
            .bodyValue(body)
            .retrieve()
            .toBodilessEntity()
            .delaySubscription(supplyDelay)
            .subscribe();
    }

    private void resetState() {
        this.gameId = null;
        this.alivePlayers = null;
    }
}
