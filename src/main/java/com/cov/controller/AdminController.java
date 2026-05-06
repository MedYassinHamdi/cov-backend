package com.cov.controller;

import com.cov.dto.request.UpdateReclamationStatusRequest;
import com.cov.dto.response.AdminStatsResponse;
import com.cov.dto.response.ReclamationResponse;
import com.cov.dto.response.TrajetResponse;
import com.cov.dto.response.UserResponse;
import com.cov.enums.StatutReclamation;
import com.cov.service.ReclamationService;
import com.cov.service.TrajetService;
import com.cov.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final TrajetService trajetService;
    private final ReclamationService reclamationService;

    public AdminController(UserService userService, TrajetService trajetService, ReclamationService reclamationService) {
        this.userService = userService;
        this.trajetService = trajetService;
        this.reclamationService = reclamationService;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.listUsers();
    }

    @GetMapping("/users/search")
    public List<UserResponse> searchUsers(@RequestParam(required = false) String q) {
        return userService.searchUsers(q);
    }

    @PutMapping("/users/{id}/bloquer")
    public UserResponse bloquer(@PathVariable Long id) {
        return userService.blockUser(id);
    }

    @PutMapping("/users/{id}/debloquer")
    public UserResponse debloquer(@PathVariable Long id) {
        return userService.unblockUser(id);
    }

    @GetMapping("/trajets")
    public List<TrajetResponse> trajets() {
        return trajetService.allTrajets();
    }

    @DeleteMapping("/trajets/{id}")
    public void deleteTrajet(@PathVariable Long id) {
        trajetService.deleteAny(id);
    }

    @GetMapping("/reclamations")
    public List<ReclamationResponse> reclamations(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) StatutReclamation statut) {
        return reclamationService.all(q, statut);
    }

    @PutMapping("/reclamations/{id}/statut")
    public ReclamationResponse updateReclamationStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateReclamationStatusRequest request) {
        return reclamationService.updateStatus(id, request.statut());
    }

    @GetMapping("/stats")
    public AdminStatsResponse stats() {
        return userService.stats();
    }
}
