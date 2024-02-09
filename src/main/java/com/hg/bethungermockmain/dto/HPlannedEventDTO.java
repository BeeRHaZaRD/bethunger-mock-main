package com.hg.bethungermockmain.dto;

import com.hg.bethungermockmain.model.HappenedEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HPlannedEventDTO extends HappenedEventDTO {
    private Long plannedEventId;

    public HPlannedEventDTO(Long plannedEventId) {
        super(HappenedEventType.PLANNED_EVENT);
        this.plannedEventId = plannedEventId;
    }
}
