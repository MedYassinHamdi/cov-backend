package com.cov.service;

import com.cov.dto.response.AdminStatsResponse;
import com.cov.dto.response.UserResponse;
import com.cov.model.Utilisateur;
import com.cov.repository.ReservationRepository;
import com.cov.repository.TrajetRepository;
import com.cov.repository.UtilisateurRepository;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final TrajetRepository trajetRepository;
    private final ReservationRepository reservationRepository;

    public UserService(UtilisateurRepository utilisateurRepository,
                       TrajetRepository trajetRepository,
                       ReservationRepository reservationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.trajetRepository = trajetRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<UserResponse> listUsers() {
        return utilisateurRepository.findAll().stream().map(DtoMapper::toUserResponse).collect(Collectors.toList());
    }

    public List<UserResponse> searchUsers(String q) {
        if (q == null || q.isBlank()) {
            return listUsers();
        }
        String keyword = q.trim().toLowerCase(Locale.ROOT);
        return utilisateurRepository.findAll().stream()
                .filter(user -> user.getEmail().toLowerCase(Locale.ROOT).contains(keyword)
                        || user.getNom().toLowerCase(Locale.ROOT).contains(keyword)
                        || user.getPrenom().toLowerCase(Locale.ROOT).contains(keyword))
                .map(DtoMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse blockUser(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateur.setActif(false);
        return DtoMapper.toUserResponse(utilisateur);
    }

    @Transactional
    public UserResponse unblockUser(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateur.setActif(true);
        return DtoMapper.toUserResponse(utilisateur);
    }

    public AdminStatsResponse stats() {
        return new AdminStatsResponse(
                utilisateurRepository.count(),
                trajetRepository.count(),
                reservationRepository.count()
        );
    }
}
