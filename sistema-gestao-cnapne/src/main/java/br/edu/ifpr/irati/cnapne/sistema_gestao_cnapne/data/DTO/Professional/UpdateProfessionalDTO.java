package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional;


import jakarta.validation.constraints.NotBlank;

public record UpdateProfessionalDTO(
        @NotBlank(message = "O e-mail é obrigatório")
        String email,

        @NotBlank(message = "O nome completo é obrigatório")
        String fullName,

        String specialty,

        @NotBlank(message = "O perfil é obrigatório")
        String role,

        boolean active
) {}

