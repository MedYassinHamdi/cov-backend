package com.cov.model;

import com.cov.enums.StatutTrajet;
import com.cov.enums.TypeTrajet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String villeDepart;

    @Column(nullable = false)
    private String villeArrivee;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    @Column(nullable = false)
    private int nbPlacesTotal;

    @Column(nullable = false)
    private int nbPlacesDisponibles;

    @Column(nullable = false)
    private double prix;

    private Integer distanceKm;

    @Enumerated(EnumType.STRING)
    private TypeTrajet typeTrajet = TypeTrajet.LEGER;

    private Boolean fumeurAutorise = false;

    private Boolean animauxAutorises = false;

    private Integer nbBagagesMax = 0;

    private String typeBagage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTrajet statut = StatutTrajet.OUVERT;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Conducteur conducteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @JsonIgnore
    @OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "trajet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avis> avis = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutTrajet.OUVERT;
        }
        if (typeTrajet == null) {
            typeTrajet = TypeTrajet.LEGER;
        }
        if (fumeurAutorise == null) {
            fumeurAutorise = false;
        }
        if (animauxAutorises == null) {
            animauxAutorises = false;
        }
        if (nbBagagesMax == null) {
            nbBagagesMax = 0;
        }
        if (nbPlacesDisponibles == 0) {
            nbPlacesDisponibles = nbPlacesTotal;
        }
    }
}
