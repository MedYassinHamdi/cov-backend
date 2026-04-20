package com.cov.model;

import com.cov.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@DiscriminatorValue("CONDUCTEUR")
@PrimaryKeyJoinColumn(name = "id")
public class Conducteur extends Voyageur {

    private String permisConduire;

    @Column(nullable = false)
    private double note = 0.0;

    @JsonIgnore
    @OneToMany(mappedBy = "conducteur")
    private List<Trajet> trajets = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "conducteur")
    private List<Vehicule> vehicules = new ArrayList<>();

    public Conducteur(String nom, String prenom, String email, String password, String telephone, String permisConduire) {
        super(nom, prenom, email, password, telephone);
        setRole(Role.CONDUCTEUR);
        this.permisConduire = permisConduire;
    }
}
