package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "responsible")
public class Responsible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Student student;
}
