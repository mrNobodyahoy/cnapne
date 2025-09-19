package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import lombok.Data;

@Data
public class ServiceResponseDTO {

    private UUID sessionId;
    private LocalDateTime createdAt;

    private LocalDate sessionDate;
    private Time sessionTime;
    private String sessionLocation;
    private String periodicity;
    private String status;

    private String typeService;
    private String descriptionService;
    private String tasks;

    private ReadStudentSummaryDTO student;

    private List<ReadProfessionalDTO> professionals;
}