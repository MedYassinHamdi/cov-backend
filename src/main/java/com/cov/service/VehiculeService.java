package com.cov.service;

import com.cov.dto.response.VehiculeResponse;
import com.cov.exception.ResourceNotFoundException;
import com.cov.model.Conducteur;
import com.cov.model.Utilisateur;
import com.cov.model.Vehicule;
import com.cov.repository.TrajetRepository;
import com.cov.repository.UtilisateurRepository;
import com.cov.repository.VehiculeRepository;
import com.cov.security.AppUserDetails;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TrajetRepository trajetRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository,
                           UtilisateurRepository utilisateurRepository,
                           TrajetRepository trajetRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.trajetRepository = trajetRepository;
    }

    @Transactional
    public VehiculeResponse add(Vehicule vehicule, AppUserDetails principal) {
        Conducteur conducteur = getConducteur(principal);
        vehicule.setId(null);
        vehicule.setConducteur(conducteur);
        return DtoMapper.toVehiculeResponse(vehiculeRepository.save(vehicule));
    }

    public List<VehiculeResponse> mesVehicules(AppUserDetails principal) {
        Conducteur conducteur = getConducteur(principal);
        return vehiculeRepository.findByConducteurId(conducteur.getId()).stream().map(DtoMapper::toVehiculeResponse).toList();
    }

    @Transactional
    public void delete(Long vehiculeId, AppUserDetails principal) {
        Conducteur conducteur = getConducteur(principal);
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule introuvable"));
        if (!Objects.equals(vehicule.getConducteur().getId(), conducteur.getId())) {
            throw new IllegalArgumentException("Ce vehicule ne vous appartient pas");
        }
        trajetRepository.findAll().stream()
                .filter(trajet -> trajet.getVehicule() != null && Objects.equals(trajet.getVehicule().getId(), vehiculeId))
                .forEach(trajet -> trajet.setVehicule(null));
        vehiculeRepository.delete(vehicule);
    }

    private Conducteur getConducteur(AppUserDetails principal) {
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        if (!(utilisateur instanceof Conducteur conducteur)) {
            throw new IllegalArgumentException("Acces conducteur uniquement");
        }
        return conducteur;
    }
}