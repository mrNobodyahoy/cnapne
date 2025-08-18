package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth;

import jakarta.validation.constraints.NotBlank;

/**
 DTO para receber as credenciais de autenticação (login e senha).
 */
public record AuthRequestDTO(
    @NotBlank(message = "O campo 'email' não pode ser vazio.")
    String email,

    @NotBlank(message = "O campo 'password' não pode ser vazio.")
    String password
) {
}
