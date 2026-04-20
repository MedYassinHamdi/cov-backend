package com.cov.repository;

import com.cov.model.Vehicule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByConducteurId(Long conducteurId);
}