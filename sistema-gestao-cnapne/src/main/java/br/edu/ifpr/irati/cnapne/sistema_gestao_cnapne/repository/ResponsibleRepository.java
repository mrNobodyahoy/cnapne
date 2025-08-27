package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Responsible;

@Repository
public interface ResponsibleRepository extends JpaRepository<Responsible, UUID> {
    
}