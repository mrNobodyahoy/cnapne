package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTeacherGuidanceDTO {

    @NotNull(message = "É obrigatório definir o local (domiciliar ou sala de aula)")
    private Boolean domiciliar;

    @NotBlank(message = "Os detalhes da orientação são obrigatórios")
    private String guidanceDetails;

    private String recommendations;
}