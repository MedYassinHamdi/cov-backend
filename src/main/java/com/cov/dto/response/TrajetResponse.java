package com.cov.dto.response;

import com.cov.enums.StatutTrajet;
import com.cov.enums.TypeTrajet;
import java.time.LocalDateTime;

public record TrajetResponse(
        Long id,
        String villeDepart,
        String villeArrivee,
        LocalDateTime dateDepart,
        int nbPlacesTotal,
        int nbPlacesDisponibles,
        double prix,
        Integer distanceKm,
        TypeTrajet typeTrajet,
        boolean fumeurAutorise,
        boolean animauxAutorises,
        int nbBagagesMax,
        String typeBagage,
        StatutTrajet statut,
        Long conducteurId,
        String conducteurNom,
        Double conducteurNote,
        Long vehiculeId,
        String vehiculeDescription,
        String vehiculeType,
        String vehiculeImageUrl,
        LocalDateTime createdAt
) {}
