package com.cov.dto.request;

import com.cov.enums.TypeTrajet;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TrajetRequest(
        @NotBlank String villeDepart,
        @NotBlank String villeArrivee,
        @NotNull @Future LocalDateTime dateDepart,
        @Min(1) int nbPlacesTotal,
        @Min(0) double prix,
        @Min(1) Integer distanceKm,
        TypeTrajet typeTrajet,
        Boolean fumeurAutorise,
        Boolean animauxAutorises,
        @Min(0) Integer nbBagagesMax,
        String typeBagage,
        Long vehiculeId
) {}
