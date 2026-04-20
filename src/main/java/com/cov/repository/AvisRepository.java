package com.cov.repository;

import com.cov.model.Avis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByConducteurId(Long conducteurId);

    List<Avis> findByTrajetId(Long trajetId);
}