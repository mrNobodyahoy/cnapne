package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    @NotBlank(message = "O nome completo é obrigatório")
    @Column(nullable = false)
    private String completeName;

    @NotBlank(message = "A matrícula é obrigatória")
    @Column(unique = true, nullable = false)
    private String registration;

    @NotBlank(message = "A turma é obrigatória")
    @Column(nullable = false)
    private String team;

    @Past(message = "A data de nascimento deve ser no passado")
    @NotNull(message = "A data de nascimento é obrigatória")
    @Column(nullable = false)
    private Date birthDate;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(nullable = false)
    private String phone;

    @Email(message = "O formato do e-mail é inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String gender;

    @NotBlank
    @Column(nullable = false)
    private String ethnicity;

    @NotBlank
    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Responsible> responsibles;

}
