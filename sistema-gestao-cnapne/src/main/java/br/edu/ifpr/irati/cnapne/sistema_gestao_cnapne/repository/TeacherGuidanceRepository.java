// src/main/java/br/edu/ifpr/irati/cnapne/sistema_gestao_cnapne/repository/TeacherGuidanceRepository.java
package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.TeacherGuidance;

@Repository
public interface TeacherGuidanceRepository
        extends JpaRepository<TeacherGuidance, UUID>, JpaSpecificationExecutor<TeacherGuidance> {
}