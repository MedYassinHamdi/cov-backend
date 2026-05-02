package com.cov.repository;

import com.cov.model.Reservation;
import com.cov.enums.StatutReservation;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVoyageurId(Long voyageurId);

    List<Reservation> findByTrajetId(Long trajetId);

    List<Reservation> findByTrajetConducteurId(Long conducteurId);

    boolean existsByVoyageurIdAndTrajetIdAndStatutIn(Long voyageurId, Long trajetId, Collection<StatutReservation> statuts);
}
