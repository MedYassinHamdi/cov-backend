package com.cov.service;

import com.cov.dto.response.AvisResponse;
import com.cov.dto.response.ReservationResponse;
import com.cov.dto.response.TrajetResponse;
import com.cov.dto.response.UserResponse;
import com.cov.dto.response.VehiculeResponse;
import com.cov.model.Avis;
import com.cov.model.Conducteur;
import com.cov.model.Reservation;
import com.cov.model.Trajet;
import com.cov.model.Utilisateur;
import com.cov.model.Vehicule;

final class DtoMapper {

    private DtoMapper() {
    }

    static UserResponse toUserResponse(Utilisateur utilisateur) {
        String permisConduire = null;
        Double note = null;
        if (utilisateur instanceof Conducteur conducteur) {
            permisConduire = conducteur.getPermisConduire();
            note = conducteur.getNote();
        }
        return new UserResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getRole(),
                utilisateur.isActif(),
                utilisateur.getDateInscription(),
                permisConduire,
                note
        );
    }

    static TrajetResponse toTrajetResponse(Trajet trajet, Long conducteurTrajets) {
        Conducteur conducteur = trajet.getConducteur();
        Vehicule vehicule = trajet.getVehicule();
        return new TrajetResponse(
                trajet.getId(),
                trajet.getVilleDepart(),
                trajet.getVilleArrivee(),
                trajet.getDateDepart(),
                trajet.getNbPlacesTotal(),
                trajet.getNbPlacesDisponibles(),
                trajet.getPrix(),
                trajet.getDistanceKm(),
                trajet.getTypeTrajet(),
                Boolean.TRUE.equals(trajet.getFumeurAutorise()),
                Boolean.TRUE.equals(trajet.getAnimauxAutorises()),
                trajet.getNbBagagesMax() == null ? 0 : trajet.getNbBagagesMax(),
                trajet.getTypeBagage(),
                trajet.getStatut(),
                conducteur != null ? conducteur.getId() : null,
                conducteur != null ? conducteur.getNom() + " " + conducteur.getPrenom() : null,
                conducteur != null ? conducteur.getNote() : null,
                conducteurTrajets,
                vehicule != null ? vehicule.getId() : null,
                vehicule != null ? vehicule.getMarque() + " " + vehicule.getModele() : null,
                vehicule != null ? vehicule.getTypeVehicule() : null,
                vehicule != null ? vehicule.getImageUrl() : null,
                trajet.getCreatedAt()
        );
    }

    static TrajetResponse toTrajetResponse(Trajet trajet) {
        return toTrajetResponse(trajet, 0L);
    }

    static ReservationResponse toReservationResponse(Reservation reservation) {
        Trajet trajet = reservation.getTrajet();
        return new ReservationResponse(
                reservation.getId(),
                trajet != null ? trajet.getId() : null,
                trajet != null ? trajet.getVilleDepart() + " -> " + trajet.getVilleArrivee() : null,
                reservation.getVoyageur() != null ? reservation.getVoyageur().getId() : null,
                reservation.getVoyageur() != null ? reservation.getVoyageur().getNom() + " " + reservation.getVoyageur().getPrenom() : null,
                reservation.getNbPlacesReservees(),
                reservation.getStatut(),
                reservation.getPenaliteMontant() == null ? 0.0 : reservation.getPenaliteMontant(),
                reservation.getPenalitePourcentage() == null ? 0 : reservation.getPenalitePourcentage(),
                reservation.getDateReservation(),
                reservation.getDateAnnulation()
        );
    }

    static VehiculeResponse toVehiculeResponse(Vehicule vehicule) {
        return new VehiculeResponse(
                vehicule.getId(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getTypeVehicule(),
                vehicule.getImmatriculation(),
                vehicule.getNbPlaces(),
                vehicule.getCouleur(),
                vehicule.getAnnee(),
                vehicule.getImageUrl(),
                vehicule.getConducteur() != null ? vehicule.getConducteur().getId() : null
        );
    }

    static AvisResponse toAvisResponse(Avis avis) {
        return new AvisResponse(
                avis.getId(),
                avis.getNote(),
                avis.getCommentaire(),
                avis.getDateAvis(),
                avis.getAuteur() != null ? avis.getAuteur().getId() : null,
                avis.getAuteur() != null ? avis.getAuteur().getNom() + " " + avis.getAuteur().getPrenom() : null,
                avis.getTrajet() != null ? avis.getTrajet().getId() : null,
                avis.getConducteur() != null ? avis.getConducteur().getId() : null
        );
    }
}
