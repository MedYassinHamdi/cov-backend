package com.cov.dto.response;

import com.cov.enums.StatutReservation;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long trajetId,
        String trajetDescription,
        Long voyageurId,
        String voyageurNom,
        int nbPlacesReservees,
        StatutReservation statut,
        double penaliteMontant,
        int penalitePourcentage,
        LocalDateTime dateReservation,
        LocalDateTime dateAnnulation
) {}
