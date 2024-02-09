package com.hg.bethungermockmain.dto;

import com.hg.bethungermockmain.model.HappenedEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HOtherEventDTO extends HappenedEventDTO {
    private Long playerId;
    private String message;

    public HOtherEventDTO(Long playerId, String message) {
        super(HappenedEventType.OTHER);
        this.playerId = playerId;
        this.message = message;
    }
}
