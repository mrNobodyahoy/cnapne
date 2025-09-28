package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Importar
import org.springframework.stereotype.Repository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID>, JpaSpecificationExecutor<Service> {
}