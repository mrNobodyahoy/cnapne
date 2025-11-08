package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher_guidances")
@Getter
@Setter
@NoArgsConstructor
public class TeacherGuidance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "É obrigatório identificar o profissional autor da orientação")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_professional_id", nullable = false)
    private Professional author;

    @OneToOne(mappedBy = "teacherGuidance", fetch = FetchType.LAZY)
    private Service service;

    @OneToOne(mappedBy = "teacherGuidance", fetch = FetchType.LAZY)
    private FollowUp followUp;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "É obrigatório definir o local (domiciliar ou sala de aula)")
    @Column(nullable = false)
    private boolean domiciliar;

    @Lob
    @Column(nullable = true)
    private String guidanceDetails;

    @Lob
    @Column(nullable = true)
    private String recommendations;
}
