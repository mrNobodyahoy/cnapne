// src/main/java/br/edu/ifpr/irati/cnapne/sistema_gestao_cnapne/data/DTO/teacherGuidance/ReadTeacherGuidanceDTO.java
package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance;

import java.time.LocalDateTime;
import java.util.UUID;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.TeacherGuidance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadTeacherGuidanceDTO {

    private UUID id;
    private LocalDateTime createdAt;
    private ReadStudentSummaryDTO student;
    private UUID serviceId;
    private UUID followUpId;
    private String guidanceDetails;
    private String recommendations;
    private boolean domiciliar;

    private ReadProfessionalDTO author;

    public ReadTeacherGuidanceDTO(TeacherGuidance guidance) {
        this.id = guidance.getId();
        this.createdAt = guidance.getCreatedAt();
        this.student = new ReadStudentSummaryDTO(guidance.getStudent());
        this.guidanceDetails = guidance.getGuidanceDetails();
        this.recommendations = guidance.getRecommendations();
        this.domiciliar = guidance.isDomiciliar();

        // Verificação de nulo OBRIGATÓRIA
        if (guidance.getService() != null) {
            this.serviceId = guidance.getService().getSessionId();
        }

        // Verificação de nulo OBRIGATÓRIA
        if (guidance.getFollowUp() != null) {
            this.followUpId = guidance.getFollowUp().getSessionId();
        }
        if (guidance.getAuthor() != null) {
            this.author = new ReadProfessionalDTO(guidance.getAuthor());
        }
    }
}