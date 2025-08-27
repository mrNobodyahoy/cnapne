package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.responsible;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Responsible;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO para representar os dados de um responsável
public record ResponsibleDTO(
    @NotBlank(message = "O nome completo do responsável é obrigatório")
    String completeName,

    @NotBlank(message = "O e-mail do responsável é obrigatório")
    @Email(message = "O formato do e-mail do responsável é inválido")
    String email,

    @NotBlank(message = "O telefone do responsável é obrigatório")
    String phone,

    @NotBlank(message = "O parentesco é obrigatório")
    String kinship
) {
    // Construtor para converter a entidade Responsible em ResponsibleDTO
    public ResponsibleDTO(Responsible responsible) {
        this(
            responsible.getCompleteName(),
            responsible.getEmail(),
            responsible.getPhone(),
            responsible.getKinship()
        );
    }
}