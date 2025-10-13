package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import lombok.Data;

@Data
public class FollowUpResponseDTO {

    private UUID sessionId;
    private LocalDateTime createdAt;

    private LocalDate sessionDate;
    private Time sessionTime;
    private String sessionLocation;
    private String status;

    private String description;
    private String tasks;

    private String areasCovered;
    private String nextSteps;

    private ReadStudentSummaryDTO student;
    private List<ReadProfessionalDTO> professionals;
}