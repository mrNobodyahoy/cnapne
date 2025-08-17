package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProfessionalDTO(
    
    @NotBlank(message = "O login é obrigatório")
    @Size(min = 3, max = 50, message = "O login deve ter entre 3 e 50 caracteres")
    String login,

    @NotBlank(message = "A senha é obrigatória")
    String password,

    @NotBlank(message = "O nome completo é obrigatório")
    String fullName,

    @Email(message = "O formato do e-mail é inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    String email,

    @NotBlank(message = "A especialidade é obrigatória")
    String specialty,

    @NotBlank(message = "O nivel de acesso é obrigatório")
    String role
) {
    
}
