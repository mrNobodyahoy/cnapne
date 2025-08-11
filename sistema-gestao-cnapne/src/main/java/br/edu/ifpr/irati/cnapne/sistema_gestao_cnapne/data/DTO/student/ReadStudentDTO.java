package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.util.Date;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;


// DTO para exibir os dados de um estudante.

public record ReadStudentDTO(
    Long id,              
    String registration,    
    String completeName,
    String team,
    String email,
    String phone,
    String status,          
    Date birthDate,         
    String gender,          
    String ethnicity        
) {
   
    public ReadStudentDTO(Student student) {
        this(
            student.getId(),
            student.getRegistration(),
            student.getCompleteName(),
            student.getTeam(),
            student.getEmail(),
            student.getPhone(),
            student.getStatus(),
            student.getBirthDate(),
            student.getGender(),
            student.getEthnicity()
        );
    }
}