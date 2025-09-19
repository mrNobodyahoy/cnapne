package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import lombok.Data;

@Data
public class ReadServiceDTO {

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

    public ReadServiceDTO(Service service) {
        this.sessionId = service.getSessionId();
        this.createdAt = service.getCreatedAt();
        this.sessionDate = service.getSessionDate();
        this.sessionTime = service.getSessionTime();
        this.sessionLocation = service.getSessionLocation();
        this.periodicity = service.getPeriodicity();
        this.status = service.getStatus();
        this.typeService = service.getTypeService();
        this.descriptionService = service.getDescriptionService();
        this.tasks = service.getTasks();

        if (service.getStudent() != null) {
            this.student = new ReadStudentSummaryDTO(
                    service.getStudent().getId(),
                    service.getStudent().getCompleteName(),
                    service.getStudent().getRegistration(),
                    service.getStudent().getTeam(),
                    service.getStudent().getStatus());
        }

        if (service.getProfessionals() != null) {
            this.professionals = service.getProfessionals().stream()
                    .map(ReadProfessionalDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
