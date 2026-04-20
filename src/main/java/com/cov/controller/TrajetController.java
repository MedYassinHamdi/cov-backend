package com.cov.controller;

import com.cov.dto.request.TrajetRequest;
import com.cov.dto.response.TrajetResponse;
import com.cov.security.AppUserDetails;
import com.cov.service.TrajetService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @GetMapping
    public List<TrajetResponse> search(
            @RequestParam(required = false) String depart,
            @RequestParam(required = false) String arrivee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer places) {
        return trajetService.search(depart, arrivee, date, places);
    }

    @GetMapping("/{id}")
    public TrajetResponse getById(@PathVariable Long id) {
        return trajetService.getById(id);
    }

    @GetMapping("/mes-trajets")
    public List<TrajetResponse> mesTrajets(Authentication authentication) {
        return trajetService.mesTrajets((AppUserDetails) authentication.getPrincipal());
    }

    @PostMapping
    public TrajetResponse create(@Valid @RequestBody TrajetRequest request, Authentication authentication) {
        return trajetService.create(request, (AppUserDetails) authentication.getPrincipal());
    }

    @PutMapping("/{id}")
    public TrajetResponse update(@PathVariable Long id, @Valid @RequestBody TrajetRequest request, Authentication authentication) {
        return trajetService.update(id, request, (AppUserDetails) authentication.getPrincipal());
    }

    @DeleteMapping("/{id}")
    public TrajetResponse cancel(@PathVariable Long id, Authentication authentication) {
        return trajetService.cancel(id, (AppUserDetails) authentication.getPrincipal());
    }
}