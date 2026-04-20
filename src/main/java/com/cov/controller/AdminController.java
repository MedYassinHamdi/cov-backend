package com.cov.controller;

import com.cov.dto.response.AdminStatsResponse;
import com.cov.dto.response.TrajetResponse;
import com.cov.dto.response.UserResponse;
import com.cov.service.TrajetService;
import com.cov.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final TrajetService trajetService;

    public AdminController(UserService userService, TrajetService trajetService) {
        this.userService = userService;
        this.trajetService = trajetService;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.listUsers();
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

    @GetMapping("/stats")
    public AdminStatsResponse stats() {
        return userService.stats();
    }
}