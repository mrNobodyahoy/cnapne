package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByLogin(String login);

    Optional<Student> findByRegistration(String registration);

    List<Student> findByCompleteNameContainingIgnoreCase(String completeName);

    List<Student> findByTeam(String team);

}
