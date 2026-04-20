package com.cov.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        @NotNull Long trajetId,
        @Min(1) int nbPlacesReservees
) {}