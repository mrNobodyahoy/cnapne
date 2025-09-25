package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "services")
@PrimaryKeyJoinColumn(name = "session_id")
public class Service extends Session {

    @NotBlank(message = "O tipo do Atendimento é obrigatório")
    @Column(nullable = false)
    private String typeService;

    @NotBlank(message = "A descrição do Atendimento é obrigatório")
    @Column(nullable = false)
    private String descriptionService;

    @Column(nullable = false)
    private String tasks;

    @ManyToMany(mappedBy = "services")
    private List<Professional> professionals = new ArrayList<>();
}
