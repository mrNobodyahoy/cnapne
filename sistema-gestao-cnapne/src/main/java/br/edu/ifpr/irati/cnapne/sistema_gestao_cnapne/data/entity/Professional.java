package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "professional")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Professional extends User {
    
    @NotBlank
    private String fullName;

    @NotBlank
    private String specialty;
    
}
