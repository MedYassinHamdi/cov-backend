package com.cov.controller;

import com.cov.dto.request.ReclamationRequest;
import com.cov.dto.response.ReclamationResponse;
import com.cov.security.AppUserDetails;
import com.cov.service.ReclamationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {

    private final ReclamationService reclamationService;

    public ReclamationController(ReclamationService reclamationService) {
        this.reclamationService = reclamationService;
    }

    @PostMapping
    public ReclamationResponse create(@Valid @RequestBody ReclamationRequest request, Authentication authentication) {
        return reclamationService.create(request, (AppUserDetails) authentication.getPrincipal());
    }

    @GetMapping("/mes-reclamations")
    public List<ReclamationResponse> mesReclamations(Authentication authentication) {
        return reclamationService.mesReclamations((AppUserDetails) authentication.getPrincipal());
    }
}
