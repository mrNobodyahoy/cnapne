package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateServiceDTO {

    @NotNull(message = "A data da Sessão é obrigatória")
    private LocalDate sessionDate;

    @NotNull(message = "O horário da Sessão é obrigatório")
    private Time sessionTime;

    @NotBlank(message = "O local da Sessão é obrigatório")
    private String sessionLocation;

    @NotBlank(message = "A periodicidade da Sessão é obrigatória")
    private String periodicity;

    @NotBlank(message = "O status da Sessão é obrigatório")
    private String status;

    @NotBlank(message = "O tipo do Atendimento é obrigatório")
    private String typeService;

    @NotBlank(message = "A descrição do Atendimento é obrigatória")
    private String descriptionService;

    @NotBlank(message = "As tarefas são obrigatórias")
    private String tasks;

    @NotNull(message = "O ID do estudante é obrigatório")
    private UUID studentId;

    @NotEmpty(message = "É necessário vincular ao menos um profissional.")
    private List<UUID> professionalIds;
}
