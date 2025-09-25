package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.CreateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.UpdateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.ProfessionalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/professionals")
@Tag(name = "Profissionais", description = "Endpoints para o gerenciamento de profissionais")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    // A injeção do ProfessionalRepository foi removida, pois não é necessária aqui.
    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    // O método createProfessional continua igual.
    @PostMapping
    public ResponseEntity<ReadProfessionalDTO> createProfessional(@Valid @RequestBody CreateProfessionalDTO dto) {
        ReadProfessionalDTO created = professionalService.createProfessional(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ✨ ESTE É O ENDPOINT CORRIGIDO PARA A LISTAGEM PAGINADA E FILTRADA
    @GetMapping
    public ResponseEntity<Page<ReadProfessionalDTO>> getAllProfessionals(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {

        // O controller apenas chama o serviço e retorna a resposta.
        Page<ReadProfessionalDTO> professionals = professionalService.findAllPaginatedAndFiltered(query, active, role,
                pageable);
        return ResponseEntity.ok(professionals);
    }

    // Os demais métodos (getById, update, delete, etc.) continuam iguais.
    @GetMapping("/me")
    public ResponseEntity<ReadProfessionalDTO> getAuthenticatedProfessional(@AuthenticationPrincipal User userDetails) {
        UUID professionalId = userDetails.getId();
        ReadProfessionalDTO professionalDTO = professionalService.getProfessionalById(professionalId);
        return ResponseEntity.ok(professionalDTO);
    }

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