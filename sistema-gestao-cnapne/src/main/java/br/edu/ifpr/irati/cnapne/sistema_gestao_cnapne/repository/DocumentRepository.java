package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByStudentId(UUID studentId);
}