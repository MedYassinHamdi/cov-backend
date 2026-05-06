package com.cov.service;

import com.cov.dto.request.ReservationRequest;
import com.cov.dto.response.ReservationResponse;
import com.cov.enums.Role;
import com.cov.enums.StatutReservation;
import com.cov.enums.StatutTrajet;
import com.cov.exception.ResourceNotFoundException;
import com.cov.model.Conducteur;
import com.cov.model.Reservation;
import com.cov.model.Trajet;
import com.cov.model.Utilisateur;
import com.cov.model.Voyageur;
import com.cov.repository.ReservationRepository;
import com.cov.repository.TrajetRepository;
import com.cov.repository.UtilisateurRepository;
import com.cov.security.AppUserDetails;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TrajetRepository trajetRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TrajetRepository trajetRepository,
                              UtilisateurRepository utilisateurRepository) {
        this.reservationRepository = reservationRepository;
        this.trajetRepository = trajetRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public ReservationResponse reserve(ReservationRequest request, AppUserDetails principal) {
        Voyageur voyageur = getVoyageur(principal);
        Trajet trajet = trajetRepository.findById(request.trajetId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable"));
        if (trajet.getStatut() != StatutTrajet.OUVERT) {
            throw new IllegalArgumentException("Ce trajet n'est pas reservable");
        }
        if (trajet.getConducteur() != null && Objects.equals(trajet.getConducteur().getId(), principal.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas reserver votre propre trajet");
        }
        boolean alreadyReserved = reservationRepository.existsByVoyageurIdAndTrajetIdAndStatutIn(
                voyageur.getId(),
                trajet.getId(),
                List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE)
        );
        if (alreadyReserved) {
            throw new IllegalArgumentException("Vous avez deja une reservation active pour ce trajet");
        }
        if (trajet.getNbPlacesDisponibles() < request.nbPlacesReservees()) {
            throw new IllegalArgumentException("Nombre de places insuffisant");
        }

        trajet.setNbPlacesDisponibles(trajet.getNbPlacesDisponibles() - request.nbPlacesReservees());
        if (trajet.getNbPlacesDisponibles() == 0) {
            trajet.setStatut(StatutTrajet.COMPLET);
        }

        Reservation reservation = new Reservation();
        reservation.setVoyageur(voyageur);
        reservation.setTrajet(trajet);
        reservation.setNbPlacesReservees(request.nbPlacesReservees());
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        return DtoMapper.toReservationResponse(reservationRepository.save(reservation));
    }

    public List<ReservationResponse> mesReservations(AppUserDetails principal) {
        return reservationRepository.findByVoyageurId(principal.getId()).stream()
                .map(DtoMapper::toReservationResponse)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> pourMesTrajets(AppUserDetails principal) {
        if (principal.getRole() != Role.CONDUCTEUR) {
            throw new IllegalArgumentException("Operation reservee aux conducteurs");
        }
        return reservationRepository.findByTrajetConducteurId(principal.getId()).stream()
                .map(DtoMapper::toReservationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse confirmer(Long reservationId, AppUserDetails principal) {
        Reservation reservation = findReservation(reservationId);
        assertOwner(reservation.getTrajet(), principal);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        return DtoMapper.toReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse refuser(Long reservationId, AppUserDetails principal) {
        Reservation reservation = findReservation(reservationId);
        assertOwner(reservation.getTrajet(), principal);
        restorePlaces(reservation);
        reservation.setStatut(StatutReservation.REFUSEE);
        reservation.setPenaliteMontant(0.0);
        reservation.setPenalitePourcentage(0);
        return DtoMapper.toReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse annuler(Long reservationId, AppUserDetails principal) {
        Reservation reservation = findReservation(reservationId);
        boolean ownerVoyageur = Objects.equals(reservation.getVoyageur().getId(), principal.getId());
        boolean ownerTrajet = reservation.getTrajet() != null && Objects.equals(reservation.getTrajet().getConducteur().getId(), principal.getId());
        if (!ownerVoyageur && !ownerTrajet) {
            throw new IllegalArgumentException("Operation non autorisee");
        }
        if (reservation.getStatut() == StatutReservation.ANNULEE || reservation.getStatut() == StatutReservation.REFUSEE) {
            throw new IllegalArgumentException("Cette reservation est deja annulee");
        }
        boolean annulationApresDepart = reservation.getTrajet() != null
                && LocalDateTime.now().isAfter(reservation.getTrajet().getDateDepart());
        if (annulationApresDepart && ownerVoyageur) {
            double montantTotal = reservation.getNbPlacesReservees() * reservation.getTrajet().getPrix();
            reservation.setPenalitePourcentage(10);
            reservation.setPenaliteMontant(Math.round(montantTotal * 10.0) / 100.0);
        } else {
            if (!annulationApresDepart) {
                restorePlaces(reservation);
            }
            reservation.setPenalitePourcentage(0);
            reservation.setPenaliteMontant(0.0);
        }
        reservation.setDateAnnulation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.ANNULEE);
        return DtoMapper.toReservationResponse(reservation);
    }

    private void restorePlaces(Reservation reservation) {
        Trajet trajet = reservation.getTrajet();
        if (trajet != null && reservation.getStatut() != StatutReservation.ANNULEE && reservation.getStatut() != StatutReservation.REFUSEE) {
            trajet.setNbPlacesDisponibles(trajet.getNbPlacesDisponibles() + reservation.getNbPlacesReservees());
            if (trajet.getStatut() == StatutTrajet.COMPLET && trajet.getNbPlacesDisponibles() > 0) {
                trajet.setStatut(StatutTrajet.OUVERT);
            }
        }
    }

    private Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation introuvable"));
    }

    private Voyageur getVoyageur(AppUserDetails principal) {
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        if (!(utilisateur instanceof Voyageur voyageur)) {
            throw new IllegalArgumentException("Utilisateur non voyageur");
        }
        return voyageur;
    }

    private void assertOwner(Trajet trajet, AppUserDetails principal) {
        if (trajet == null || !Objects.equals(trajet.getConducteur().getId(), principal.getId())) {
            throw new IllegalArgumentException("Operation autorisee uniquement au conducteur proprietaire");
        }
    }
}
