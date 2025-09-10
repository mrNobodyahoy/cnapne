package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {

        List<Professional> findByFullNameContainingIgnoreCase(String fullName);

        List<Professional> findByProfileName(Role role);

        List<Professional> findByActive(boolean active);

}