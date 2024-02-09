package com.hg.bethungermockmain.dto;

import com.hg.bethungermockmain.model.HappenedEventType;
import com.hg.bethungermockmain.model.PlayerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HPlayerEventDTO extends HappenedEventDTO {
    private PlayerEventType playerEventType;
    private Long playerId;

    public HPlayerEventDTO(PlayerEventType playerEventType, Long playerId) {
        super(HappenedEventType.PLAYER);
        this.playerEventType = playerEventType;
        this.playerId = playerId;
    }
}
