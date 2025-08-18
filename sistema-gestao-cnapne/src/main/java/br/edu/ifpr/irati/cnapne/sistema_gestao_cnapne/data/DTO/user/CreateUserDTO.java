package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserDTO(
        @NotBlank(message = "O campo 'login' não pode ser vazio.")
        String email,

        @NotBlank(message = "O campo 'password' não pode ser vazio.")
        String password,

        @NotNull(message = "O campo 'role' é obrigatório.")
        Role role
) { }

