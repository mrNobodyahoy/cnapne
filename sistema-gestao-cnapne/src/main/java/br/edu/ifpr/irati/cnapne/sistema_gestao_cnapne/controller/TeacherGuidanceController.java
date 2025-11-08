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

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.CreateTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.ReadTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.UpdateTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.TeacherGuidanceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orientacoes-professor")
public class TeacherGuidanceController {

    @Autowired
    private TeacherGuidanceService teacherGuidanceService;

    @PostMapping
    public ResponseEntity<ReadTeacherGuidanceDTO> createGuidance(
            @Valid @RequestBody CreateTeacherGuidanceDTO createDto) {

        ReadTeacherGuidanceDTO savedGuidance = teacherGuidanceService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedGuidance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadTeacherGuidanceDTO> getGuidanceById(@PathVariable UUID id) {
        ReadTeacherGuidanceDTO guidance = teacherGuidanceService.getById(id);
        return ResponseEntity.ok(guidance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadTeacherGuidanceDTO> updateGuidance(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeacherGuidanceDTO updateDto) {

        ReadTeacherGuidanceDTO updatedGuidance = teacherGuidanceService.update(id, updateDto);
        return ResponseEntity.ok(updatedGuidance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuidance(@PathVariable UUID id) {
        teacherGuidanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReadTeacherGuidanceDTO>> listGuidances(
            @RequestParam(value = "studentName", required = false) String studentName,
            @RequestParam(value = "domiciliar", required = false) Boolean domiciliar,
            Pageable pageable) {

        User authenticatedUser = teacherGuidanceService.getAuthenticatedUser();

        Page<ReadTeacherGuidanceDTO> page = teacherGuidanceService.findAllPaginated(
                studentName,
                domiciliar,
                pageable,
                authenticatedUser);

        return ResponseEntity.ok(page);
    }
}
