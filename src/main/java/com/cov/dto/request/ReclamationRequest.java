package com.cov.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReclamationRequest(
        @NotBlank String objet,
        @NotBlank String message,
        Long reservationId
) {}
