package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;

public record ReadProfessionalDTO(
        String email,
        String fullName,
        String specialty,
        String role,
        boolean active) {
    public ReadProfessionalDTO(Professional professional) {
        this(
                professional.getEmail(),
                professional.getFullName(),
                professional.getSpecialty(),
                professional.getProfile().getName().name(),
                professional.isActive());
    }
}
