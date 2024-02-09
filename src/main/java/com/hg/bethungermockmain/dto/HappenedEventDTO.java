package com.hg.bethungermockmain.dto;

import com.hg.bethungermockmain.model.HappenedEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class HappenedEventDTO {
    private HappenedEventType type;
}
