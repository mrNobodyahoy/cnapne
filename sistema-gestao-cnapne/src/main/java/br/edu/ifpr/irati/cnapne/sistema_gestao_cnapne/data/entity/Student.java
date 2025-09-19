package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
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
    private LocalDate birthDate;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(nullable = false)
    private String phone;

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

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

}
