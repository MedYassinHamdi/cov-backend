package com.cov.model;

import com.cov.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@DiscriminatorValue("VOYAGEUR")
@PrimaryKeyJoinColumn(name = "id")
public class Voyageur extends Utilisateur {

    @JsonIgnore
    @OneToMany(mappedBy = "voyageur")
    private List<Reservation> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "auteur")
    private List<Avis> avisDonnes = new ArrayList<>();

    public Voyageur(String nom, String prenom, String email, String password, String telephone) {
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setPassword(password);
        setTelephone(telephone);
        setRole(Role.VOYAGEUR);
    }
}
