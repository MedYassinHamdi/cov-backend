package com.cov.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AvisRequest(
        @NotNull Long trajetId,
        @Min(1) @Max(5) int note,
        @NotBlank String commentaire
) {}