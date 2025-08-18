package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);


}
