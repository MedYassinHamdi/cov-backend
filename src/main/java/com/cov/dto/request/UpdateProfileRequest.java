package com.cov.dto.request;

public record UpdateProfileRequest(
        String nom,
        String prenom,
        String telephone
) {}
