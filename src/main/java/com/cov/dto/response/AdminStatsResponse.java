package com.cov.dto.response;

public record AdminStatsResponse(
        long nbUsers,
        long nbTrajets,
        long nbReservations
) {}