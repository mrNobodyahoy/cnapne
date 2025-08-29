package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.document.ReadDocumentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Document;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.DocumentService;

@RestController
@RequestMapping("/api/v1/students/{studentId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<ReadDocumentDTO> uploadDocument(@PathVariable UUID studentId, @RequestParam("file") MultipartFile file, @RequestParam("type") String documentType) throws IOException {
        ReadDocumentDTO saved = documentService.uploadDocument(studentId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ReadDocumentDTO>> getDocuments(@PathVariable UUID studentId) {
        return ResponseEntity.ok(documentService.getDocumentsByStudent(studentId));
    }

    // MÉTODO ÚNICO E OTIMIZADO PARA PEGAR O ARQUIVO
    @GetMapping("/{docId}")
    public ResponseEntity<Resource> getDocument(
            @PathVariable UUID studentId,
            @PathVariable UUID docId,
            @RequestParam(value = "disposition", defaultValue = "inline") String disposition) {
        
        Document doc = documentService.getDocumentByIdAndStudentId(docId, studentId);
        
        try {
            Path filePath = Paths.get(doc.getPathFile());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                // Valida o parâmetro para segurança
                String validDisposition = "attachment".equalsIgnoreCase(disposition) ? "attachment" : "inline";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, validDisposition + "; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Não foi possível ler o arquivo!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo!", e);
        }
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID studentId, @PathVariable UUID docId) {
        documentService.deleteDocument(docId);
        return ResponseEntity.noContent().build();
    }
}