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
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.CreateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.UpdateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.ProfessionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/professionals")
@Tag(name = "Profissionais", description = "Endpoints para o gerenciamento de profissionais")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @Operation(summary = "Criar um novo profissional", description = "Este endpoint cria um novo profissional.", responses = {
            @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReadProfessionalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de dados: E-mail já em uso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ReadProfessionalDTO> createProfessional(@Valid @RequestBody CreateProfessionalDTO dto) {
        ReadProfessionalDTO created = professionalService.createProfessional(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // --- LÓGICA DE BUSCA E FILTRO ALTERADA ---

    @Operation(summary = "Listar todos os profissionais", description = "Retorna uma lista com todos os profissionais cadastrados.")
    @GetMapping
    public ResponseEntity<List<ReadProfessionalDTO>> getAllProfessionals() {
        return ResponseEntity.ok(professionalService.getAllProfessionals());
    }

    @Operation(summary = "Buscar profissionais por nome", description = "Realiza uma busca por parte do nome completo do profissional.")
    @GetMapping("/search")
    public ResponseEntity<List<ReadProfessionalDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(professionalService.search(query));
    }

    @Operation(summary = "Filtrar profissionais por status ou perfil", description = "Retorna uma lista de profissionais com base no status 'active' (true ou false) OU em um perfil específico (ex: 'COORDENADOR'). Forneça apenas um parâmetro de filtro por vez.")
    @GetMapping("/filter")
    public ResponseEntity<?> filterProfessionals(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String role) {

        // Filtra por status se o parâmetro 'active' for fornecido
        if (active != null) {
            List<ReadProfessionalDTO> professionals = professionalService.findByActive(active);
            return ResponseEntity.ok(professionals);
        }

        // Filtra por perfil se o parâmetro 'role' for fornecido
        if (role != null && !role.isBlank()) {
            try {
                Role roleEnum = Role.valueOf(role.toUpperCase());
                List<ReadProfessionalDTO> professionals = professionalService.findByRole(roleEnum);
                return ResponseEntity.ok(professionals);
            } catch (IllegalArgumentException e) {
                // Retorna erro se o valor do perfil for inválido
                ApiErrorDTO error = new ApiErrorDTO(HttpStatus.BAD_REQUEST, "O perfil (role) '" + role + "' é inválido.");
                return ResponseEntity.badRequest().body(error);
            }
        }
        
        // Retorna um erro se nenhum parâmetro de filtro for usado
        ApiErrorDTO error = new ApiErrorDTO(HttpStatus.BAD_REQUEST, "Você deve fornecer um parâmetro de filtro ('active' ou 'role').");
        return ResponseEntity.badRequest().body(error);
    }

    @Operation(summary = "Obter dados do profissional autenticado", description = "Retorna os detalhes do profissional que está logado no sistema.")
    @GetMapping("/me")
    public ResponseEntity<ReadProfessionalDTO> getAuthenticatedProfessional(@AuthenticationPrincipal User userDetails) {
        UUID professionalId = userDetails.getId();
        ReadProfessionalDTO professionalDTO = professionalService.getProfessionalById(professionalId);
        return ResponseEntity.ok(professionalDTO);
    }
    
    // --- FIM DA LÓGICA ALTERADA ---

    @GetMapping("/{id}")
    public ResponseEntity<ReadProfessionalDTO> getProfessionalById(@PathVariable UUID id) {
        return ResponseEntity.ok(professionalService.getProfessionalById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadProfessionalDTO> updateProfessional(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfessionalDTO dto) {
        ReadProfessionalDTO updated = professionalService.updateProfessional(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable UUID id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
}