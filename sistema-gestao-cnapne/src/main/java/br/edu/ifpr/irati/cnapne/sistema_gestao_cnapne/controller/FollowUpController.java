package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.CreateFollowUpDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.FollowUpResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.ReadFollowUpDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.UpdateFollowUpDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.FollowUpService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/acompanhamentos")
public class FollowUpController {
    @Autowired
    private FollowUpService followUpService;

    @PostMapping
    public ResponseEntity<FollowUpResponseDTO> createAcompanhamento(
            @Valid @RequestBody CreateFollowUpDTO createDto) {

        FollowUpResponseDTO savedFollowUp = followUpService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedFollowUp);
    }

    @GetMapping
    public ResponseEntity<List<ReadFollowUpDTO>> listarAcompanhamentos() {
        List<ReadFollowUpDTO> acompanhamentos = followUpService.getAllFollowUps();
        return ResponseEntity.ok(acompanhamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadFollowUpDTO> buscarAcompanhamentoPorId(@PathVariable UUID id) {
        ReadFollowUpDTO acompanhamento = followUpService.getFollowUpById(id);
        return ResponseEntity.ok(acompanhamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadFollowUpDTO> atualizarAcompanhamento(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFollowUpDTO updateDto) {
        ReadFollowUpDTO updatedFollowUp = followUpService.update(id, updateDto);
        return ResponseEntity.ok(updatedFollowUp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAcompanhamento(@PathVariable UUID id) {
        followUpService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<ReadFollowUpDTO>> listarAcompanhamentosPorProfissional(
            @PathVariable UUID professionalId) {
        List<ReadFollowUpDTO> followUps = followUpService.getFollowUpByProfessional(professionalId);
        return ResponseEntity.ok(followUps);
    }
}
