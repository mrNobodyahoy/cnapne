package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimelineItemDTO {

    private UUID id;
    private String type;
    private String title;
    private LocalDate date;
    private String status;
    private List<String> professionalNames;

    public TimelineItemDTO(Service service) {
        this.id = service.getSessionId();
        this.type = "ATENDIMENTO";
        this.title = service.getTypeService();
        this.date = service.getSessionDate();
        this.status = service.getStatus();
        this.professionalNames = service.getProfessionals().stream()
                .map(prof -> prof.getFullName())
                .collect(Collectors.toList());
    }

    public TimelineItemDTO(FollowUp followUp) {
        this.id = followUp.getSessionId();
        this.type = "ACOMPANHAMENTO";
        this.title = followUp.getDescription();
        this.date = followUp.getSessionDate();
        this.status = followUp.getStatus();
        this.professionalNames = followUp.getProfessionals().stream()
                .map(prof -> prof.getFullName())
                .collect(Collectors.toList());
    }
}