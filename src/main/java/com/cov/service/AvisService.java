package com.cov.service;

import com.cov.dto.request.AvisRequest;
import com.cov.dto.response.AvisResponse;
import com.cov.enums.StatutReservation;
import com.cov.exception.ResourceNotFoundException;
import com.cov.model.Avis;
import com.cov.model.Conducteur;
import com.cov.model.Reservation;
import com.cov.model.Trajet;
import com.cov.model.Utilisateur;
import com.cov.model.Voyageur;
import com.cov.repository.AvisRepository;
import com.cov.repository.ReservationRepository;
import com.cov.repository.TrajetRepository;
import com.cov.repository.UtilisateurRepository;
import com.cov.security.AppUserDetails;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvisService {

    private final AvisRepository avisRepository;
    private final TrajetRepository trajetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ReservationRepository reservationRepository;

    public AvisService(AvisRepository avisRepository,
                       TrajetRepository trajetRepository,
                       UtilisateurRepository utilisateurRepository,
                       ReservationRepository reservationRepository) {
        this.avisRepository = avisRepository;
        this.trajetRepository = trajetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public AvisResponse create(AvisRequest request, AppUserDetails principal) {
        Voyageur auteur = getVoyageur(principal);
        Trajet trajet = trajetRepository.findById(request.trajetId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet introuvable"));
        Reservation reservation = reservationRepository.findByVoyageurId(auteur.getId()).stream()
                .filter(item -> item.getTrajet() != null && item.getTrajet().getId().equals(trajet.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune reservation trouvee pour ce trajet"));
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new IllegalArgumentException("Vous pouvez commenter uniquement une reservation confirmee");
        }
        if (avisRepository.existsByAuteurIdAndTrajetId(auteur.getId(), trajet.getId())) {
            throw new IllegalArgumentException("Vous avez deja laisse un avis pour ce trajet");
        }

        Avis avis = new Avis();
        avis.setAuteur(auteur);
        avis.setTrajet(trajet);
        avis.setConducteur(trajet.getConducteur());
        avis.setNote(request.note());
        avis.setCommentaire(request.commentaire());

        Avis saved = avisRepository.save(avis);
        updateDriverRating(trajet.getConducteur());
        return DtoMapper.toAvisResponse(saved);
    }

    public List<AvisResponse> byConducteur(Long conducteurId) {
        return avisRepository.findByConducteurId(conducteurId).stream().map(DtoMapper::toAvisResponse).toList();
    }

    private void updateDriverRating(Conducteur conducteur) {
        List<Avis> avisList = avisRepository.findByConducteurId(conducteur.getId());
        double average = avisList.stream().mapToInt(Avis::getNote).average().orElse(0.0);
        conducteur.setNote(Math.round(average * 10.0) / 10.0);
    }

    private Voyageur getVoyageur(AppUserDetails principal) {
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        if (!(utilisateur instanceof Voyageur voyageur)) {
            throw new IllegalArgumentException("Acces voyageur uniquement");
        }
        return voyageur;
    }
}
