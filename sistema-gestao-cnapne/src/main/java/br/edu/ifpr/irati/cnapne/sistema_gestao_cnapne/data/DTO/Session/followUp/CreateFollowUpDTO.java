package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFollowUpDTO {

    @NotNull(message = "A data da Sessão é obrigatória")
    private LocalDate sessionDate;

    @NotNull(message = "O horário da Sessão é obrigatório")
    private Time sessionTime;

    @NotBlank(message = "O local da Sessão é obrigatório")
    private String sessionLocation;

    @NotBlank(message = "O status da Sessão é obrigatório")
    private String status;

    @NotNull(message = "O ID do estudante é obrigatório")
    private UUID studentId;

    @NotBlank(message = "A descrição do Acompanhamento é obrigatória")
    private String description;

    private String areasCovered;

    @NotBlank(message = "As tarefas são obrigatórias")
    private String tasks;

    @NotEmpty(message = "É necessário vincular ao menos um profissional.")
    private List<UUID> professionalIds;
}