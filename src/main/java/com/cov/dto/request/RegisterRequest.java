package com.cov.dto.request;

import com.cov.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @Email @NotBlank String email,
        @NotBlank String password,
        String telephone,
        String permisConduire,
        Role role
) {}
