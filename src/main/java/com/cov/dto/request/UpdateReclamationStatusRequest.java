package com.cov.dto.request;

import com.cov.enums.StatutReclamation;
import jakarta.validation.constraints.NotNull;

public record UpdateReclamationStatusRequest(
        @NotNull StatutReclamation statut
) {}
