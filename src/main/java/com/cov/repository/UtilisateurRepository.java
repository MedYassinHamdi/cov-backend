package com.cov.repository;

import com.cov.model.Utilisateur;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByActifTrue();
}