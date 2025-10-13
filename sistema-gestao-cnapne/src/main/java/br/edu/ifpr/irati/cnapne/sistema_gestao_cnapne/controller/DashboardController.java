package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.DashboardResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.MonthlyDataDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.StudentStatusDataDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @AuthenticationPrincipal User userDetails) {

        UUID professionalId = null;
        if (userDetails.getProfile().getName() != Role.COORDENACAO_CNAPNE) {
            professionalId = userDetails.getId();
        }

        DashboardResponseDTO dashboard = dashboardService.getDashboard(professionalId);
        return ResponseEntity.ok(dashboard);
    }

    @Transactional(readOnly = true)
    @GetMapping("/evolucao-mensal")
    public ResponseEntity<List<MonthlyDataDTO>> getMonthlyEvolution(
            @AuthenticationPrincipal User userDetails) {

        UUID professionalId = null;
        if (userDetails.getProfile().getName() != Role.COORDENACAO_CNAPNE) {
            professionalId = userDetails.getId();
        }

        List<MonthlyDataDTO> evolutionData = dashboardService.getMonthlyEvolution(professionalId);
        return ResponseEntity.ok(evolutionData);
    }

    @GetMapping("/student-status")
    public ResponseEntity<List<StudentStatusDataDTO>> getStudentStatus() {
        List<StudentStatusDataDTO> statusData = dashboardService.getStudentStatusDistribution();
        return ResponseEntity.ok(statusData);
    }
}
