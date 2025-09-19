package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

    List<FollowUp> findByStudentId(UUID studentId);

    List<FollowUp> findByStudentCompleteNameContainingIgnoreCase(String studentName);

    List<FollowUp> findByProfessionalsId(UUID professionalId);

}
