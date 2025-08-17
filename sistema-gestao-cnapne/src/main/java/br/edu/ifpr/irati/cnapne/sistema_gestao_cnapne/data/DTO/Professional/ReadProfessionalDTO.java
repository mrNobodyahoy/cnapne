package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;

public record ReadProfessionalDTO(
    String login,
    String fullName,
    String email,
    String specialty,
    String role,
    boolean active
) {
    public ReadProfessionalDTO(Professional professional) {
        this(
            professional.getLogin(),
            professional.getFullName(),
            professional.getEmail(),
            professional.getSpecialty(),
            professional.getProfile().getName().name(),
            professional.isActive()
        );
    }
}
