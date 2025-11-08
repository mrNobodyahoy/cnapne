package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.ReadTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadFollowUpDTO {

    private UUID sessionId;
    private LocalDateTime createdAt;
    private LocalDate sessionDate;
    private Time sessionTime;
    private String sessionLocation;
    private String status;
    private String description;
    private String tasks;

    private ReadTeacherGuidanceDTO teacherGuidance;
    private String areasCovered;
    private String nextSteps;

    private ReadStudentSummaryDTO student;
    private List<ReadProfessionalDTO> professionals;

    public ReadFollowUpDTO(FollowUp followUp) {
        this.sessionId = followUp.getSessionId();
        this.createdAt = followUp.getCreatedAt();
        this.sessionDate = followUp.getSessionDate();
        this.sessionTime = followUp.getSessionTime();
        this.sessionLocation = followUp.getSessionLocation();
        this.status = followUp.getStatus();
        this.description = followUp.getDescription();
        this.tasks = followUp.getTasks();
        this.areasCovered = followUp.getAreasCovered();
        this.nextSteps = followUp.getNextSteps();

        if (followUp.getStudent() != null) {
            this.student = new ReadStudentSummaryDTO(
                    followUp.getStudent().getId(),
                    followUp.getStudent().getCompleteName(),
                    followUp.getStudent().getRegistration(),
                    followUp.getStudent().getTeam(),
                    followUp.getStudent().getStatus());
        }

        if (followUp.getProfessionals() != null) {
            this.professionals = followUp.getProfessionals().stream()
                    .map(ReadProfessionalDTO::new)
                    .collect(Collectors.toList());
        }
        if (followUp.getTeacherGuidance() != null) {
            this.teacherGuidance = new ReadTeacherGuidanceDTO(followUp.getTeacherGuidance());
        }
    }

}