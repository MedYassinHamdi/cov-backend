package com.cov.dto.response;

public record VehiculeResponse(
        Long id,
        String marque,
        String modele,
        String immatriculation,
        int nbPlaces,
        String couleur,
        int annee,
        String imageUrl,
        Long conducteurId
) {}