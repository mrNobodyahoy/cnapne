package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID>, JpaSpecificationExecutor<Service> {
    long countByStatus(String status);

    long countByProfessionals_Id(UUID professionalId);

    long countByProfessionals_IdAndStatus(UUID professionalId, String status);

    Optional<Service> findFirstByOrderBySessionDateAsc();

}