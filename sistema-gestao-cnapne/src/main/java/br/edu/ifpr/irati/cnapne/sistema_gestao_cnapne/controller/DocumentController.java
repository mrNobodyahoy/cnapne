package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.*;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.document.ReadDocumentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.DocumentService;

@RestController
@RequestMapping("/api/v1/students/{studentId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<ReadDocumentDTO> uploadDocument(
            @PathVariable UUID studentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String documentType) throws IOException {

        ReadDocumentDTO saved = documentService.uploadDocument(studentId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ReadDocumentDTO>> getDocuments(@PathVariable UUID studentId) {
        return ResponseEntity.ok(documentService.getDocumentsByStudent(studentId));
    }

    @GetMapping("/{docId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID studentId, @PathVariable UUID docId) {
        List<ReadDocumentDTO> docs = documentService.getDocumentsByStudent(studentId);
        ReadDocumentDTO doc = docs.stream().filter(d -> d.id().equals(docId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        Resource resource = new FileSystemResource(doc.pathFile());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.fileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID studentId, @PathVariable UUID docId) {
        documentService.deleteDocument(docId);
        return ResponseEntity.noContent().build();
    }
}
