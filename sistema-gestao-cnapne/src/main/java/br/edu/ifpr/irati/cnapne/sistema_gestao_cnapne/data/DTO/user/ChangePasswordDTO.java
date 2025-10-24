package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 6) String newPassword) {
}
