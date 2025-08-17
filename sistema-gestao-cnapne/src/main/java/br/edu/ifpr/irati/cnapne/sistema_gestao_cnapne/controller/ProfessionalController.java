package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.CreateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.ProfessionalService;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.ApiErrorDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/professionals")
@Tag(name = "Profissionais", description = "Endpoints para o gerenciamento de profissionais")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @Operation(
        summary = "Criar um novo profissional",
        description = "Este endpoint cria um novo profissional ou retorna erro se o login/email já estiverem em uso.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReadProfessionalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de dados: Login ou e-mail já em uso", 
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
        }
    )
    @PostMapping
    public ResponseEntity<ReadProfessionalDTO> createProfessional(@Valid @RequestBody CreateProfessionalDTO dto) {
        ReadProfessionalDTO created = professionalService.createProfessional(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ReadProfessionalDTO>> getAllProfessionals() {
        return ResponseEntity.ok(professionalService.getAllStudents()); // método já retorna List<ReadProfessionalDTO>
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadProfessionalDTO> getProfessionalById(@PathVariable UUID id) {
        return ResponseEntity.ok(professionalService.getProfessionalById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadProfessionalDTO> updateProfessional(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProfessionalDTO dto) {
        ReadProfessionalDTO updated = professionalService.updateProfessional(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable UUID id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
