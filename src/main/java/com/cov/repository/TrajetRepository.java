package com.cov.repository;

import com.cov.model.Trajet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    List<Trajet> findByConducteurId(Long conducteurId);
}