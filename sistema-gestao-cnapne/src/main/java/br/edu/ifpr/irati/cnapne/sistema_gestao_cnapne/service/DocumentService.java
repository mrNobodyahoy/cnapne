package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.document.ReadDocumentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Document;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.DocumentRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final StudentRepository studentRepository;

    public DocumentService(DocumentRepository documentRepository, StudentRepository studentRepository) {
        this.documentRepository = documentRepository;
        this.studentRepository = studentRepository;
    }

    public ReadDocumentDTO uploadDocument(UUID studentId, MultipartFile file, String documentType) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Estudante não encontrado"));

        String uploadDir = "uploads/student/" + studentId;
        Path path = Paths.get(uploadDir);
        Files.createDirectories(path);

        String filePath = path.resolve(file.getOriginalFilename()).toString();
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setDocumentType(documentType);
        doc.setAttachmentDate(new Date());
        doc.setPathFile(filePath);
        doc.setStudent(student);

        Document saved = documentRepository.save(doc);

        return new ReadDocumentDTO(
                saved.getId(),
                saved.getFileName(),
                saved.getDocumentType(),
                saved.getAttachmentDate(),
                saved.getPathFile());
    }

    public List<ReadDocumentDTO> getDocumentsByStudent(UUID studentId) {
        return documentRepository.findByStudentId(studentId).stream()
                .map(d -> new ReadDocumentDTO(d.getId(), d.getFileName(), d.getDocumentType(), d.getAttachmentDate(),
                        d.getPathFile()))
                .toList();
    }

    public void deleteDocument(UUID docId) {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new DataNotFoundException("Documento não encontrado"));
        String caminhoDoArquivo = doc.getPathFile();
        System.out.println("--- TENTANDO DELETAR O ARQUIVO EM: " + caminhoDoArquivo + " ---");
        try {
            Files.deleteIfExists(Paths.get(doc.getPathFile()));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao excluir arquivo físico", e);
        }
        documentRepository.delete(doc);
 
   }

   public Document getDocumentByIdAndStudentId(UUID docId, UUID studentId) {
        return documentRepository.findByIdAndStudentId(docId, studentId)
                .orElseThrow(() -> new DataNotFoundException("Documento não encontrado ou não pertence a este estudante."));
    }
}