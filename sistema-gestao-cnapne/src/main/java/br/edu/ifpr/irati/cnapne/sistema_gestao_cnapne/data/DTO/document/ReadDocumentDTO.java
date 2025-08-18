package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.document;

import java.util.Date;
import java.util.UUID;

public record ReadDocumentDTO(
        UUID id,
        String fileName,
        String documentType,
        Date attachmentDate,
        String pathFile) {
}
