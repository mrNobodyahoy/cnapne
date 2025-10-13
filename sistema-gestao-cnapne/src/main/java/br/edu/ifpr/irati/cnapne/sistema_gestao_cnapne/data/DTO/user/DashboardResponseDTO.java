package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {

    private long totalAtendimentos;
    private long atendimentosAgendados;
    private long atendimentosRealizados;
    private long atendimentosCancelados;

    private long totalAcompanhamentos;
    private long acompanhamentosAgendados;
    private long acompanhamentosRealizados;
    private long acompanhamentosCancelados;
}
