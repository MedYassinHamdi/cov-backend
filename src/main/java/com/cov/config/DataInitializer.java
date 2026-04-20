package com.cov.config;

import com.cov.model.Admin;
import com.cov.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAdmin(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (utilisateurRepository.findByEmail("admin@cov.local").isEmpty()) {
                Admin admin = new Admin(
                        "Admin",
                        "System",
                        "admin@cov.local",
                        passwordEncoder.encode("admin123"),
                        "0000000000"
                );
                utilisateurRepository.save(admin);
            }
        };
    }
}