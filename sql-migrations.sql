-- ============================================================================
-- SQL Migration: Add new fields to existing tables
-- ============================================================================
-- Run these ALTER statements against your MySQL database
-- New tables (Reclamation) will be auto-created by Hibernate (ddl-auto = update)
-- ============================================================================

-- 1. ALTER TABLE trajet - Add advanced trip criteria
ALTER TABLE trajet 
ADD COLUMN distance_km INT DEFAULT NULL,
ADD COLUMN type_trajet VARCHAR(255) NOT NULL DEFAULT 'LEGER',
ADD COLUMN fumeur_autorise BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN animaux_autorises BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN nb_bagages_max INT NOT NULL DEFAULT 0,
ADD COLUMN type_bagage VARCHAR(255) DEFAULT NULL;

-- 2. ALTER TABLE reservation - Add cancellation penalty tracking
ALTER TABLE reservation 
ADD COLUMN penalite_montant DOUBLE NOT NULL DEFAULT 0.0,
ADD COLUMN penalite_pourcentage INT NOT NULL DEFAULT 0,
ADD COLUMN date_annulation DATETIME DEFAULT NULL;

-- 3. ALTER TABLE vehicule - Add vehicle type
ALTER TABLE vehicule 
ADD COLUMN type_vehicule VARCHAR(255) DEFAULT NULL;

-- ============================================================================
-- Reclamation table will be auto-created by Hibernate with these columns:
-- id BIGINT PRIMARY KEY AUTO_INCREMENT
-- objet VARCHAR(255) NOT NULL
-- message VARCHAR(2000) NOT NULL
-- statut VARCHAR(255) NOT NULL DEFAULT 'OUVERTE'
-- date_creation DATETIME
-- date_mise_a_jour DATETIME
-- auteur_id BIGINT NOT NULL (FK to utilisateurs.id)
-- reservation_id BIGINT (FK to reservation.id)
-- ============================================================================
