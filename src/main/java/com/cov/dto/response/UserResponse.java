package com.cov.dto.response;

import com.cov.enums.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        Role role,
        boolean actif,
        LocalDateTime dateInscription,
        String permisConduire,
        Double note
) {}