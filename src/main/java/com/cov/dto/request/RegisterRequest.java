package com.cov.dto.request;

import com.cov.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @Email @NotBlank String email,
        @NotBlank String password,
        String telephone,
        String permisConduire,
        @NotNull Role role
) {}