package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.ApiErrorDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.CreateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.UpdateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Estudantes", description = "Endpoints para o gerenciamento de estudantes") // Tag para agrupar endpoints
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Criar um novo estudante", description = "Este endpoint cria um novo estudante ou reativa um estudante 'Arquivado' se a matrícula já existir. "
            +
            "Requer permissão de Coordenador.", responses = {
                    @ApiResponse(responseCode = "201", description = "Estudante criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReadStudentDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Conflito de dados: Login ou matrícula já em uso por um estudante ativo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
            })
    @PostMapping
    public ResponseEntity<ReadStudentDTO> createStudent(@Valid @RequestBody CreateStudentDTO dto) {
        ReadStudentDTO createdStudent = studentService.createStudent(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping
    public ResponseEntity<List<ReadStudentDTO>> getAllStudent() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadStudentDTO> getStudentById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadStudentDTO> updateStudent(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateStudentDTO dto) {
        ReadStudentDTO updated = studentService.updateStudent(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReadStudentSummaryDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(studentService.search(query));
    }

    @Operation(summary = "Filtrar estudantes por status", description = "Retorna uma lista de estudantes com base em um status específico (ex: 'ATIVO' ou 'INATIVO').")
    @GetMapping("/filter")
    public ResponseEntity<List<ReadStudentSummaryDTO>> getStudentsByStatus(@RequestParam String status) {
        List<ReadStudentSummaryDTO> students = studentService.findByStatus(status);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/me")
    public ResponseEntity<ReadStudentDTO> getAuthenticatedStudent(@AuthenticationPrincipal User userDetails) {
        UUID studentId = userDetails.getId();
        ReadStudentDTO studentDTO = studentService.getStudentById(studentId);
        return ResponseEntity.ok(studentDTO);
    }

}
