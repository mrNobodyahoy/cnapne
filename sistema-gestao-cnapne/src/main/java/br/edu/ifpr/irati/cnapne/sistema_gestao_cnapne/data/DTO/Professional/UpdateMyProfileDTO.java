package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional;

import jakarta.validation.constraints.NotBlank;

// Este DTO representa os campos que o usuário PODE alterar no seu próprio perfil
public class UpdateMyProfileDTO {

    @NotBlank(message = "O nome completo é obrigatório")
    private String fullName;

    @NotBlank(message = "A especialidade é obrigatória")
    private String specialty;

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    // Setters (se necessário)
}
