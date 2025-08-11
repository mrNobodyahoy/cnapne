package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    
    Optional<Professional> findByEmail(String email);
}
