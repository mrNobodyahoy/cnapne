// Crie este novo arquivo:
// src/main/java/br/edu/ifpr/irati/cnapne/sistema_gestao_cnapne/data/DTO/student/UpdateStudentDTO.java

package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.util.Date;
import jakarta.validation.constraints.*;

public record UpdateStudentDTO(
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    String email,

    @NotBlank(message = "O nome completo é obrigatório.")
    String completeName, 

    @NotBlank(message = "A matrícula é obrigatória.")
    String registration,

    @NotBlank(message = "A turma é obrigatória.")
    String team,

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser no passado.")
    Date birthDate,

    @NotBlank(message = "O telefone é obrigatório.")
    String phone,

    @NotBlank(message = "O gênero é obrigatório.")
    String gender,

    @NotBlank(message = "A etnia é obrigatória.")
    String ethnicity
) {}