package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profile")
public class Profile implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role name;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name.name();
    }

}
