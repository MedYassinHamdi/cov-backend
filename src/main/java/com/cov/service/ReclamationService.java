package com.cov.service;

import com.cov.dto.request.ReclamationRequest;
import com.cov.dto.response.ReclamationResponse;
import com.cov.enums.StatutReclamation;
import com.cov.exception.ResourceNotFoundException;
import com.cov.model.Reclamation;
import com.cov.model.Reservation;
import com.cov.model.Utilisateur;
import com.cov.repository.ReclamationRepository;
import com.cov.repository.ReservationRepository;
import com.cov.repository.UtilisateurRepository;
import com.cov.security.AppUserDetails;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ReservationRepository reservationRepository;

    public ReclamationService(ReclamationRepository reclamationRepository,
                              UtilisateurRepository utilisateurRepository,
                              ReservationRepository reservationRepository) {
        this.reclamationRepository = reclamationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReclamationResponse create(ReclamationRequest request, AppUserDetails principal) {
        Utilisateur auteur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Reservation reservation = null;
        if (request.reservationId() != null) {
            reservation = reservationRepository.findById(request.reservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation introuvable"));
            boolean isVoyageur = reservation.getVoyageur() != null && Objects.equals(reservation.getVoyageur().getId(), auteur.getId());
            boolean isConducteur = reservation.getTrajet() != null
                    && reservation.getTrajet().getConducteur() != null
                    && Objects.equals(reservation.getTrajet().getConducteur().getId(), auteur.getId());
            if (!isVoyageur && !isConducteur) {
                throw new IllegalArgumentException("Cette reservation ne vous concerne pas");
            }
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setObjet(request.objet().trim());
        reclamation.setMessage(request.message().trim());
        reclamation.setAuteur(auteur);
        reclamation.setReservation(reservation);
        reclamation.setStatut(StatutReclamation.OUVERTE);

        return toResponse(reclamationRepository.save(reclamation));
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> mesReclamations(AppUserDetails principal) {
        return reclamationRepository.findByAuteurId(principal.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> all(String q, StatutReclamation statut) {
        String keyword = q == null ? null : q.trim().toLowerCase(Locale.ROOT);
        return reclamationRepository.findAll().stream()
                .filter(reclamation -> statut == null || reclamation.getStatut() == statut)
                .filter(reclamation -> keyword == null
                        || keyword.isBlank()
                        || reclamation.getObjet().toLowerCase(Locale.ROOT).contains(keyword)
                        || reclamation.getMessage().toLowerCase(Locale.ROOT).contains(keyword))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ReclamationResponse updateStatus(Long id, StatutReclamation statut) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation introuvable"));
        reclamation.setStatut(statut);
        return toResponse(reclamation);
    }

    private ReclamationResponse toResponse(Reclamation reclamation) {
        Utilisateur auteur = reclamation.getAuteur();
        return new ReclamationResponse(
                reclamation.getId(),
                reclamation.getObjet(),
                reclamation.getMessage(),
                reclamation.getStatut(),
                auteur != null ? auteur.getId() : null,
                auteur != null ? auteur.getNom() + " " + auteur.getPrenom() : null,
                reclamation.getReservation() != null ? reclamation.getReservation().getId() : null,
                reclamation.getDateCreation(),
                reclamation.getDateMiseAJour()
        );
    }
}
