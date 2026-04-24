package com.cov.controller;

import com.cov.dto.response.VehiculeResponse;
import com.cov.model.Vehicule;
import com.cov.security.AppUserDetails;
import com.cov.service.VehiculeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    public VehiculeResponse add(@Valid @RequestBody Vehicule vehicule, Authentication authentication) {
        return vehiculeService.add(vehicule, (AppUserDetails) authentication.getPrincipal());
    }

    @GetMapping("/mes-vehicules")
    public List<VehiculeResponse> mesVehicules(Authentication authentication) {
        return vehiculeService.mesVehicules((AppUserDetails) authentication.getPrincipal());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        vehiculeService.delete(id, (AppUserDetails) authentication.getPrincipal());
    }

    @PostMapping("/{id}/image")
    public VehiculeResponse uploadImage(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file, Authentication authentication) {
        return vehiculeService.uploadImage(id, file, (AppUserDetails) authentication.getPrincipal());
    }
}