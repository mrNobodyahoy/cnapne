package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "professional")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Professional extends User {
    
    @NotBlank
    private String fullName;

    @Email(message = "O formato do e-mail é inválido")
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String specialty;
    
}
