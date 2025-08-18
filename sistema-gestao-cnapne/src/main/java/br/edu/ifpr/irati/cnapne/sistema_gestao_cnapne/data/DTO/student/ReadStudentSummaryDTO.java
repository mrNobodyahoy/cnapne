package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.util.UUID;

public record ReadStudentSummaryDTO(
        UUID id,
        String completeName,
        String registration
) {}
