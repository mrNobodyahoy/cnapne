package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.util.Date;

public record UpdateStudentDTO(

    @Size(min = 3, max = 50, message = "O login deve ter entre 3 e 50 caracteres.")
    String login,

    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    String password,

    @Size(max = 255, message = "O nome completo não deve exceder 255 caracteres.")
    String completeName, 

    @Size(max = 50, message = "A matrícula não deve exceder 50 caracteres.")
    String registration,

    @Size(max = 100, message = "A turma não deve exceder 100 caracteres.")
    String team,

    @Past(message = "A data de nascimento deve ser no passado.")
    Date birthDate,

    String phone,

    @Email(message = "O formato do e-mail é inválido.")
    String email,

    String gender,

    String ethnicity,
    
    String status
) {}
