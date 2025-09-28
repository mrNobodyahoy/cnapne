package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.CreateServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.ReadServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.ServiceResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.UpdateServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.AtendimentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/atendimentos")
public class ServiceController {

    @Autowired
    private AtendimentoService atendimentoService;

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createAtendimento(
            @Valid @RequestBody CreateServiceDTO createDto) {

        ServiceResponseDTO savedService = atendimentoService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadServiceDTO> buscarAtendimentoPorId(@PathVariable UUID id) {
        ReadServiceDTO atendimento = atendimentoService.getServiceById(id);
        return ResponseEntity.ok(atendimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadServiceDTO> atualizarAtendimento(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceDTO updateDto) {
        ReadServiceDTO updatedService = atendimentoService.update(id, updateDto);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtendimento(@PathVariable UUID id) {
        atendimentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReadServiceDTO>> listarAtendimentos(
            @RequestParam(value = "studentName", required = false) String studentName,
            @RequestParam(value = "status", required = false) String status,
            Pageable pageable) {

        Page<ReadServiceDTO> page = atendimentoService.findAllPaginated(studentName, status, pageable);
        return ResponseEntity.ok(page);
    }

}
