package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentStatusDataDTO {
    private String status;
    private long count;
}