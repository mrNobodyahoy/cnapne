package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para receber as credenciais de autenticação (login e senha).
 * Segue o padrão solicitado, adaptado para usar 'login'.
 *
 * @param login O nome de usuário para autenticação.
 * @param password A senha do usuário.
 */
public record AuthRequestDTO(
    @NotBlank(message = "O campo 'login' não pode ser vazio.")
    String login,

    @NotBlank(message = "O campo 'password' não pode ser vazio.")
    String password
) {
}
