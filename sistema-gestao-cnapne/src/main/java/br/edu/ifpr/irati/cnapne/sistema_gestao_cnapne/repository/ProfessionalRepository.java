package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;

public interface ProfessionalRepository
                extends JpaRepository<Professional, UUID>, JpaSpecificationExecutor<Professional> {
}