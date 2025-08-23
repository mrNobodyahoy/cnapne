package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

public record AuthResponseDTO(
        String token,
        String email,
        Role role
) {}
