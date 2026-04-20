package com.cov.controller;

import com.cov.dto.request.AvisRequest;
import com.cov.dto.response.AvisResponse;
import com.cov.security.AppUserDetails;
import com.cov.service.AvisService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/avis")
public class AvisController {

    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @PostMapping
    public AvisResponse create(@Valid @RequestBody AvisRequest request, Authentication authentication) {
        return avisService.create(request, (AppUserDetails) authentication.getPrincipal());
    }

    @GetMapping("/conducteur/{id}")
    public List<AvisResponse> byConducteur(@PathVariable Long id) {
        return avisService.byConducteur(id);
    }
}