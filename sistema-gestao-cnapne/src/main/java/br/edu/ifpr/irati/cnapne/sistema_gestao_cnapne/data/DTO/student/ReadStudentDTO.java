package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID; // <<< MUDANÇA AQUI
import java.util.stream.Collectors;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.responsible.ResponsibleDTO; // <<< MUDANÇA AQUI
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student; // <<< MUDANÇA AQUI

public record ReadStudentDTO(
        UUID id,
        String registration,
        String completeName,
        String team,
        String email,
        String phone,
        String status,
        LocalDate birthDate,
        String gender,
        String ethnicity,
        List<ResponsibleDTO> responsibles) {

    public ReadStudentDTO(Student student) {
        this(
                student.getId(),
                student.getRegistration(),
                student.getCompleteName(),
                student.getTeam(),
                student.getEmail(),
                student.getPhone(),
                student.getStatus(),
                student.getBirthDate(),
                student.getGender(),
                student.getEthnicity(),
                student.getResponsibles() != null ? student.getResponsibles().stream()
                        .map(ResponsibleDTO::new)
                        .collect(Collectors.toList()) : List.of());
    }
}