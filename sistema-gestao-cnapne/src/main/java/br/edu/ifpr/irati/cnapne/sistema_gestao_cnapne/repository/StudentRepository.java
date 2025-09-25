package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {

        Optional<Student> findByRegistration(String registration);

        List<Student> findByCompleteNameContainingIgnoreCase(String completeName);

        List<Student> findByTeam(String team);

        List<Student> findByRegistrationContainingIgnoreCase(String registration);

        Page<Student> findByStatusAndCompleteNameStartingWithIgnoreCaseOrStatusAndRegistrationContainingIgnoreCase(
                        String status1, String name, String status2, String registration, Pageable pageable);

        Page<Student> findByCompleteNameStartingWithIgnoreCaseOrRegistrationContainingIgnoreCase(
                        String name, String registration, Pageable pageable);

        Page<Student> findByStatus(String status, Pageable pageable);
}