package com.cov.service;

import com.cov.dto.request.BecomeConducteurRequest;
import com.cov.dto.request.LoginRequest;
import com.cov.dto.request.RegisterRequest;
import com.cov.dto.response.AuthResponse;
import com.cov.dto.response.UserResponse;
import com.cov.enums.Role;
import com.cov.model.Conducteur;
import com.cov.model.Utilisateur;
import com.cov.repository.UtilisateurRepository;
import com.cov.security.AppUserDetails;
import com.cov.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EntityManager entityManager;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       EntityManager entityManager) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.entityManager = entityManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new IllegalArgumentException("L'inscription admin n'est pas autorisee");
        }
        if (utilisateurRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Un compte avec cet email existe deja");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        Utilisateur utilisateur = new Conducteur(
                request.nom(),
                request.prenom(),
                request.email(),
                encodedPassword,
                request.telephone(),
                request.permisConduire()
        );
        Utilisateur saved = utilisateurRepository.save(utilisateur);
        AppUserDetails principal = new AppUserDetails(saved.getId(), saved.getEmail(), saved.getPassword(), saved.isActif(), saved.getRole());
        String token = jwtUtil.generateToken(principal, saved.getId(), saved.getRole().name());
        return new AuthResponse(token, DtoMapper.toUserResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        String token = jwtUtil.generateToken(principal, utilisateur.getId(), utilisateur.getRole().name());
        return new AuthResponse(token, DtoMapper.toUserResponse(utilisateur));
    }

    public UserResponse me(Authentication authentication) {
        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return DtoMapper.toUserResponse(utilisateur);
    }

    @Transactional
    public AuthResponse becomeConducteur(BecomeConducteurRequest request, Authentication authentication) {
        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        Utilisateur utilisateur = utilisateurRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (utilisateur.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Un administrateur ne peut pas devenir conducteur");
        }

        if (!(utilisateur instanceof Conducteur)) {
            String permis = request == null ? null : request.permisConduire();
            entityManager.createNativeQuery(
                    "insert into conducteur (id, note, permis_conduire) values (:id, 0.0, :permis)"
            )
                    .setParameter("id", utilisateur.getId())
                    .setParameter("permis", permis)
                    .executeUpdate();
            entityManager.createNativeQuery(
                    "update utilisateurs set role = 'CONDUCTEUR', type_utilisateur = 'CONDUCTEUR' where id = :id"
            )
                    .setParameter("id", utilisateur.getId())
                    .executeUpdate();
            entityManager.flush();
            entityManager.clear();
            utilisateur = utilisateurRepository.findById(principal.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        }

        AppUserDetails upgradedPrincipal = new AppUserDetails(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getPassword(),
                utilisateur.isActif(),
                Role.CONDUCTEUR
        );
        String token = jwtUtil.generateToken(upgradedPrincipal, utilisateur.getId(), Role.CONDUCTEUR.name());
        return new AuthResponse(token, DtoMapper.toUserResponse(utilisateur));
    }
}
