package com.cov.service;

import com.cov.dto.request.TrajetRequest;
import com.cov.dto.response.TrajetResponse;
import com.cov.enums.Role;
import com.cov.enums.StatutTrajet;
import com.cov.enums.TypeTrajet;
import com.cov.exception.ResourceNotFoundException;
import com.cov.model.Conducteur;
import com.cov.model.Trajet;
import com.cov.model.Utilisateur;
import com.cov.model.Vehicule;
import com.cov.repository.TrajetRepository;
import com.cov.repository.UtilisateurRepository;
import com.cov.repository.VehiculeRepository;
import com.cov.security.AppUserDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrajetService {

    private final TrajetRepository trajetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeRepository vehiculeRepository;

    public TrajetService(TrajetRepository trajetRepository,
                         UtilisateurRepository utilisateurRepository,
                         VehiculeRepository vehiculeRepository) {
        this.trajetRepository = trajetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<TrajetResponse> search(String depart,
                                       String arrivee,
                                       LocalDate date,
                                       Integer places,
                                       TypeTrajet typeTrajet,
                                       Boolean fumeur,
                                       Boolean animaux,
                                       String typeVehicule,
                                       StatutTrajet statut,
                                       Double prixMin,
                                       Double prixMax,
                                       Double noteMin,
                                       Integer distanceKmMin,
                                       Integer distanceKmMax,
                                       LocalTime heureDepartMin,
                                       LocalTime heureDepartMax) {
        LocalDateTime now = LocalDateTime.now();
        return trajetRepository.findAll().stream()
                .filter(trajet -> statut == null ? trajet.getStatut() == StatutTrajet.OUVERT : trajet.getStatut() == statut)
                .filter(trajet -> statut == null || trajet.getDateDepart().isAfter(now))
                .filter(trajet -> depart == null || depart.isBlank() || trajet.getVilleDepart().toLowerCase().contains(depart.toLowerCase()))
                .filter(trajet -> arrivee == null || arrivee.isBlank() || trajet.getVilleArrivee().toLowerCase().contains(arrivee.toLowerCase()))
                .filter(trajet -> date == null || trajet.getDateDepart().toLocalDate().isEqual(date))
                .filter(trajet -> places == null || trajet.getNbPlacesDisponibles() >= places)
                .filter(trajet -> typeTrajet == null || trajet.getTypeTrajet() == typeTrajet)
                .filter(trajet -> fumeur == null || Objects.equals(Boolean.TRUE.equals(trajet.getFumeurAutorise()), fumeur))
                .filter(trajet -> animaux == null || Objects.equals(Boolean.TRUE.equals(trajet.getAnimauxAutorises()), animaux))
                .filter(trajet -> typeVehicule == null || typeVehicule.isBlank()
                        || (trajet.getVehicule() != null
                        && trajet.getVehicule().getTypeVehicule() != null
                        && trajet.getVehicule().getTypeVehicule().toLowerCase(Locale.ROOT).contains(typeVehicule.toLowerCase(Locale.ROOT))))
                .filter(trajet -> prixMin == null || trajet.getPrix() >= prixMin)
                .filter(trajet -> prixMax == null || trajet.getPrix() <= prixMax)
                .filter(trajet -> noteMin == null || (trajet.getConducteur() != null && trajet.getConducteur().getNote() >= noteMin))
                .filter(trajet -> distanceKmMin == null || (trajet.getDistanceKm() != null && trajet.getDistanceKm() >= distanceKmMin))
                .filter(trajet -> distanceKmMax == null || (trajet.getDistanceKm() != null && trajet.getDistanceKm() <= distanceKmMax))
                .filter(trajet -> heureDepartMin == null || !trajet.getDateDepart().toLocalTime().isBefore(heureDepartMin))
                .filter(trajet -> heureDepartMax == null || !trajet.getDateDepart().toLocalTime().isAfter(heureDepartMax))
                .map(DtoMapper::toTrajetResponse)
                .collect(Collectors.toList());
    }

    public TrajetResponse getById(Long id) {
        return DtoMapper.toTrajetResponse(findTrajet(id));
    }

    public List<TrajetResponse> mesTrajets(AppUserDetails principal) {
        return trajetRepository.findByConducteurId(principal.getId()).stream().map(DtoMapper::toTrajetResponse).collect(Collectors.toList());
    }

    public List<TrajetResponse> allTrajets() {
        return trajetRepository.findAll().stream().map(DtoMapper::toTrajetResponse).collect(Collectors.toList());
    }

    @Transactional
    public TrajetResponse create(TrajetRequest request, AppUserDetails principal) {
        Conducteur conducteur = getConducteur(principal);
        validateRoute(request);
        Vehicule vehicule = resolveVehicle(request, conducteur.getId());

        Trajet trajet = new Trajet();
        trajet.setVilleDepart(request.villeDepart().trim());
        trajet.setVilleArrivee(request.villeArrivee().trim());
        trajet.setDateDepart(request.dateDepart());
        trajet.setNbPlacesTotal(request.nbPlacesTotal());
        trajet.setNbPlacesDisponibles(request.nbPlacesTotal());
        trajet.setPrix(request.prix());
        trajet.setDistanceKm(request.distanceKm());
        trajet.setTypeTrajet(request.typeTrajet() == null ? TypeTrajet.LEGER : request.typeTrajet());
        trajet.setFumeurAutorise(Boolean.TRUE.equals(request.fumeurAutorise()));
        trajet.setAnimauxAutorises(Boolean.TRUE.equals(request.animauxAutorises()));
        trajet.setNbBagagesMax(request.nbBagagesMax() == null ? 0 : request.nbBagagesMax());
        trajet.setTypeBagage(request.typeBagage());
        trajet.setStatut(StatutTrajet.OUVERT);
        trajet.setConducteur(conducteur);
        trajet.setVehicule(vehicule);
        return DtoMapper.toTrajetResponse(trajetRepository.save(trajet));
    }

    @Transactional
    public TrajetResponse update(Long id, TrajetRequest request, AppUserDetails principal) {
        Trajet trajet = findTrajet(id);
        assertOwner(trajet, principal);
        validateRoute(request);
        Vehicule vehicule = resolveVehicle(request, trajet.getConducteur().getId());

        trajet.setVilleDepart(request.villeDepart().trim());
        trajet.setVilleArrivee(request.villeArrivee().trim());
        trajet.setDateDepart(request.dateDepart());
        trajet.setNbPlacesTotal(request.nbPlacesTotal());
        trajet.setNbPlacesDisponibles(Math.min(trajet.getNbPlacesDisponibles(), request.nbPlacesTotal()));
        trajet.setPrix(request.prix());
        trajet.setDistanceKm(request.distanceKm());
        trajet.setTypeTrajet(request.typeTrajet() == null ? TypeTrajet.LEGER : request.typeTrajet());
        trajet.setFumeurAutorise(Boolean.TRUE.equals(request.fumeurAutorise()));
        trajet.setAnimauxAutorises(Boolean.TRUE.equals(request.animauxAutorises()));
        trajet.setNbBagagesMax(request.nbBagagesMax() == null ? 0 : request.nbBagagesMax());
        trajet.setTypeBagage(request.typeBagage());
        trajet.setVehicule(vehicule);
        return DtoMapper.toTrajetResponse(trajet);
    }

    @Transactional
    public TrajetResponse cancel(Long id, AppUserDetails principal) {
        Trajet trajet = findTrajet(id);
        assertOwner(trajet, principal);
        trajet.setStatut(StatutTrajet.ANNULE);
        return DtoMapper.toTrajetResponse(trajet);
    }

    @Transactional
    public void deleteAny(Long id) {
        if (!trajetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trajet introuvable");
        }
        trajetRepository.deleteById(id);
    }

    private Trajet findTrajet(Long id) {
        return trajetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable"));
    }

    private Conducteur getConducteur(AppUserDetails principal) {
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        if (utilisateur.getRole() != Role.CONDUCTEUR || !(utilisateur instanceof Conducteur conducteur)) {
            throw new IllegalArgumentException("Acces conducteur uniquement");
        }
        return conducteur;
    }

    private void validateRoute(TrajetRequest request) {
        String depart = request.villeDepart().trim();
        String arrivee = request.villeArrivee().trim();
        if (depart.equalsIgnoreCase(arrivee)) {
            throw new IllegalArgumentException("La ville de depart et la destination doivent etre differentes");
        }
    }

    private Vehicule resolveVehicle(TrajetRequest request, Long conducteurId) {
        Vehicule vehicule = request.vehiculeId() == null ? null : vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule introuvable"));
        if (vehicule != null && !Objects.equals(vehicule.getConducteur().getId(), conducteurId)) {
            throw new IllegalArgumentException("Ce vehicule ne vous appartient pas");
        }
        if (vehicule != null && request.nbPlacesTotal() > vehicule.getNbPlaces()) {
            throw new IllegalArgumentException("Le nombre de places depasse la capacite du vehicule");
        }
        return vehicule;
    }

    private void assertOwner(Trajet trajet, AppUserDetails principal) {
        if (trajet.getConducteur() == null || !Objects.equals(trajet.getConducteur().getId(), principal.getId())) {
            throw new IllegalArgumentException("Operation autorisee uniquement au conducteur proprietaire");
        }
    }
}
