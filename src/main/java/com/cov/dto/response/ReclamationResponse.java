package com.cov.dto.response;

import com.cov.enums.StatutReclamation;
import java.time.LocalDateTime;

public record ReclamationResponse(
        Long id,
        String objet,
        String message,
        StatutReclamation statut,
        Long auteurId,
        String auteurNom,
        Long reservationId,
        LocalDateTime dateCreation,
        LocalDateTime dateMiseAJour
) {}
