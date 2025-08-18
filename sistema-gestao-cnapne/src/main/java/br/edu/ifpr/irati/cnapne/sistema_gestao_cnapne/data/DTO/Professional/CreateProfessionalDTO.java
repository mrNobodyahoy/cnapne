package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateProfessionalDTO(

    @Email(message = "O formato do e-mail é inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    String password,

    @NotBlank(message = "O nome completo é obrigatório")
    String fullName,


    @NotBlank(message = "A especialidade é obrigatória")
    String specialty,

    @NotBlank(message = "O nivel de acesso é obrigatório")
    String role
) {
    
}
