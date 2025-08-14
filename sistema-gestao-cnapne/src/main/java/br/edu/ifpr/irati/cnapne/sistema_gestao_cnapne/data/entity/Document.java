package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;

    @NotBlank(message = "O nome do arquivo é obrigatório")
    @Column(nullable = false)
    private String fileName;

    @NotBlank(message = "O tipo do documento é obrigatório")
    @Column(nullable = false)
    private String documentType;

    @NotNull(message = "A data do anexo é obrigatória")
    @Column(nullable = false)
    private Date attachmentDate;

    @NotBlank(message = "O caminho do arquivo é obrigatório")
    @Column(nullable = false)
    private String pathFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
