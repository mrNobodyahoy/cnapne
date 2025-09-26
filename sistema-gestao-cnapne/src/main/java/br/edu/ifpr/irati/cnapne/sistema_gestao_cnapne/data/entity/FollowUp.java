package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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
@Table(name = "followups")
@PrimaryKeyJoinColumn(name = "session_id")
public class FollowUp extends Session {

    @NotBlank(message = "A descrição do acompanhamento é obrigatório")
    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private String areasCovered;

    @Lob
    @Column(nullable = true)
    private String nextSteps;

    @Lob
    @Column(nullable = false)
    private String tasks;

    @ManyToMany(mappedBy = "followUps")
    private List<Professional> professionals = new ArrayList<>();
}