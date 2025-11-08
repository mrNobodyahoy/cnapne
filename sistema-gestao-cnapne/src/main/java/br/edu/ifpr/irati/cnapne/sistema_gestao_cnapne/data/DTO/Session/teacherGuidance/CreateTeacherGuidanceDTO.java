// src/main/java/br/edu/ifpr/irati/cnapne/sistema_gestao_cnapne/data/DTO/teacherGuidance/CreateTeacherGuidanceDTO.java
package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeacherGuidanceDTO {

    private UUID serviceId;

    private UUID followUpId;

    @NotBlank(message = "Os detalhes da orientação são obrigatórios")
    private String guidanceDetails;

    @NotBlank(message = "Os detalhes da orientação são obrigatórios")
    private String recommendations;

    @NotNull(message = "É obrigatório definir o local (domiciliar ou sala de aula)")
    private Boolean domiciliar; // Usar Boolean (wrapper) permite a validação @NotNull
    //
}