package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "responsible")
public class Responsible {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;

    @NotBlank(message = "O nome completo do responsável é obrigatório")
    @Column(nullable = false)
    private String completeName;

    @Email(message = "O formato do e-mail do responsável é inválido")
    @NotBlank(message = "O e-mail do responsável é obrigatório")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "O telefone do responsável é obrigatório")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "O parentesco é obrigatório")
    @Column(nullable = false)
    private String kinship;

    @NotNull(message = "O estudante é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    private Student student;
}
