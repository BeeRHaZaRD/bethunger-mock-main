package com.hg.bethungermockmain.dto;

import jakarta.validation.constraints.NotNull;

public record SupplyRequestDTO(
    @NotNull
    Long id,

    @NotNull
    Long playerId,

    @NotNull
    Long itemId
) {}
