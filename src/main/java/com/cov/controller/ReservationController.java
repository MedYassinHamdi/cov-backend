package com.cov.controller;

import com.cov.dto.request.ReservationRequest;
import com.cov.dto.response.ReservationResponse;
import com.cov.security.AppUserDetails;
import com.cov.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ReservationResponse reserve(@Valid @RequestBody ReservationRequest request, Authentication authentication) {
        return reservationService.reserve(request, (AppUserDetails) authentication.getPrincipal());
    }

    @GetMapping("/mes-reservations")
    public List<ReservationResponse> mesReservations(Authentication authentication) {
        return reservationService.mesReservations((AppUserDetails) authentication.getPrincipal());
    }

    @PutMapping("/{id}/confirmer")
    public ReservationResponse confirmer(@PathVariable Long id, Authentication authentication) {
        return reservationService.confirmer(id, (AppUserDetails) authentication.getPrincipal());
    }

    @PutMapping("/{id}/annuler")
    public ReservationResponse annuler(@PathVariable Long id, Authentication authentication) {
        return reservationService.annuler(id, (AppUserDetails) authentication.getPrincipal());
    }

    @PutMapping("/{id}/refuser")
    public ReservationResponse refuser(@PathVariable Long id, Authentication authentication) {
        return reservationService.refuser(id, (AppUserDetails) authentication.getPrincipal());
    }
}