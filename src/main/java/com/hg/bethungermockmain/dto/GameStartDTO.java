package com.hg.bethungermockmain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GameStartDTO(
    @NotNull
    @Size(min = 2, max = 24)
    List<Long> playersId
) {}
