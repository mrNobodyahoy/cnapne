package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    
    Optional<Professional> findByEmail(String email);
}
