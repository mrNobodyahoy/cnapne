package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.util.UUID;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;

public record ReadStudentSummaryDTO(
                UUID id,
                String completeName,
                String registration,
                String team,
                String status) {
        // 2. Adicione este construtor de conveniência
        public ReadStudentSummaryDTO(Student student) {
                this(
                                student.getId(),
                                student.getCompleteName(),
                                student.getRegistration(),
                                student.getTeam(),
                                student.getStatus());
        }
}