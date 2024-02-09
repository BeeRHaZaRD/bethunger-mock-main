package com.hg.bethungermockmain.dto;

import jakarta.validation.constraints.NotNull;

public record PlannedEventRequestDTO(
    @NotNull
    Long id,

    @NotNull
    Long eventTypeId
) {}
