package com.cov.dto.response;

import java.time.LocalDateTime;

public record AvisResponse(
        Long id,
        int note,
        String commentaire,
        LocalDateTime dateAvis,
        Long auteurId,
        String auteurNom,
        Long trajetId,
        Long conducteurId
) {}