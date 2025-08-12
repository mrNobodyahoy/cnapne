package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.CreateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.ApiErrorDTO;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Estudantes", description = "Endpoints para o gerenciamento de estudantes") // Tag para agrupar endpoints na UI do Swagger
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(
        summary = "Criar um novo estudante",
        description = "Este endpoint cria um novo estudante ou reativa um estudante 'Arquivado' se a matrícula já existir. " +
                      "Requer permissão de Coordenador.",
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "Estudante criado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReadStudentDTO.class))
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Dados inválidos fornecidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Conflito de dados: Login ou matrícula já em uso por um estudante ativo",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            )
        }
    )
    @PostMapping
    public ResponseEntity<ReadStudentDTO> createStudent(@Valid @RequestBody CreateStudentDTO dto) {
        // 1. Chama o serviço para executar a lógica de negócio
        ReadStudentDTO createdStudent = studentService.createStudent(dto);
        
        // 2. Retorna a resposta HTTP com status 201 (Created) e o DTO do estudante criado
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    // ... outros métodos do controller (GET, PUT, DELETE) viriam aqui
}
