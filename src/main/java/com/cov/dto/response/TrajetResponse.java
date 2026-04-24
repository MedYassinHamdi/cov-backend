package com.cov.dto.response;

import com.cov.enums.StatutTrajet;
import java.time.LocalDateTime;

public record TrajetResponse(
        Long id,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        int nbPlacesTotal,
        int nbPlacesDisponibles,
        double prix,
        StatutTrajet statut,
        Long conducteurId,
        String conducteurNom,
        Long vehiculeId,
        String vehiculeDescription,
        String vehiculeImageUrl,
        LocalDateTime createdAt
) {}