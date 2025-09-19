package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.time.LocalDate;
import java.util.List;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.responsible.ResponsibleDTO;
import jakarta.validation.constraints.Email; // <<< MUDANÇA AQUI
import jakarta.validation.constraints.NotBlank; // <<< MUDANÇA AQUI
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record CreateStudentDTO(

        @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "O formato do e-mail é inválido.") String email,

        @NotBlank(message = "A senha é obrigatória.") @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.") String password,

        @NotBlank(message = "O nome completo é obrigatório.") @Size(max = 255) String completeName,

        @NotBlank(message = "A matrícula é obrigatória.") @Size(max = 50) String registration,

        @NotBlank(message = "A turma é obrigatória.") @Size(max = 100) String team,

        @NotNull(message = "A data de nascimento é obrigatória.") @Past(message = "A data de nascimento deve ser no passado.") LocalDate birthDate,

        @NotBlank(message = "O telefone é obrigatório.") String phone,

        @NotBlank(message = "O gênero é obrigatório.") String gender,

        @NotBlank(message = "A etnia é obrigatória.") String ethnicity,

        List<ResponsibleDTO> responsibles) {
}