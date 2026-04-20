package com.cov.repository;

import com.cov.model.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVoyageurId(Long voyageurId);

    List<Reservation> findByTrajetId(Long trajetId);
}