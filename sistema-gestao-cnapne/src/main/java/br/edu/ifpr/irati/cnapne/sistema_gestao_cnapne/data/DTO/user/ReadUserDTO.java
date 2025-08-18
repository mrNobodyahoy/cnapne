package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

public record ReadUserDTO(
        String email,

        String password,

        boolean active,

        Role role

) {

    public ReadUserDTO(User user) {
        this(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                user.getProfile().getName());
    }

}
