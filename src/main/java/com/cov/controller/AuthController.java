package com.cov.controller;

import com.cov.dto.request.BecomeConducteurRequest;
import com.cov.dto.request.LoginRequest;
import com.cov.dto.request.RegisterRequest;
import com.cov.dto.request.UpdateProfileRequest;
import com.cov.dto.response.AuthResponse;
import com.cov.dto.response.UserResponse;
import com.cov.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return authService.me(authentication);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@RequestBody UpdateProfileRequest request, Authentication authentication) {
        return authService.updateProfile(request, authentication);
    }

    @PostMapping("/become-conducteur")
    public AuthResponse becomeConducteur(@RequestBody BecomeConducteurRequest request, Authentication authentication) {
        return authService.becomeConducteur(request, authentication);
    }
}
