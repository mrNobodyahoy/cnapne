package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

/**
 * DTO para enviar a resposta da autenticação.
 * Segue o padrão solicitado, incluindo o token e a role do usuário.
 */
public record AuthResponseDTO(
    String token,
    Role role
) {
}
