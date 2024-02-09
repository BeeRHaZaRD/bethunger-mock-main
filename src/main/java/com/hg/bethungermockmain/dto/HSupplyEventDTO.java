package com.hg.bethungermockmain.dto;

import com.hg.bethungermockmain.model.HappenedEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HSupplyEventDTO extends HappenedEventDTO {
    private Long supplyId;

    public HSupplyEventDTO(Long supplyId) {
        super(HappenedEventType.SUPPLY);
        this.supplyId = supplyId;
    }
}
