package com.cov.dto.response;

public record AuthResponse(
        String token,
        UserResponse user
) {}