package com.cov.model;

import com.cov.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("ADMIN")
@PrimaryKeyJoinColumn(name = "id")
public class Admin extends Utilisateur {

    public Admin(String nom, String prenom, String email, String password, String telephone) {
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setPassword(password);
        setTelephone(telephone);
        setRole(Role.ADMIN);
    }
}
