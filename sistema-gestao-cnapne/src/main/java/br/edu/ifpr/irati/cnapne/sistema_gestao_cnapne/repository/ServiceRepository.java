package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    List<Service> findByStudentId(UUID studentId);

    List<Service> findByStudentCompleteNameContainingIgnoreCase(String studentName);

    List<Service> findByProfessionalsId(UUID professionalId);

}