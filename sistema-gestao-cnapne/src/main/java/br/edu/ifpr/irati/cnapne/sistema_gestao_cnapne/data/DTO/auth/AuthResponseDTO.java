package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

/*
  DTO para enviar a resposta da autenticação.
 */
public record AuthResponseDTO(
    String token,
    Role role
) {
}
