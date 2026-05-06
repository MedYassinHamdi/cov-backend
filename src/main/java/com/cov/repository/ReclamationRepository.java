package com.cov.repository;

import com.cov.enums.StatutReclamation;
import com.cov.model.Reclamation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByAuteurId(Long auteurId);

    List<Reclamation> findByStatut(StatutReclamation statut);
}
