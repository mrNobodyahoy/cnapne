package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.DashboardResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.MonthlyDataDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.StudentStatusDataDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.FollowUpRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ServiceRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;

@org.springframework.stereotype.Service
public class DashboardService {

        @Autowired
        private ServiceRepository serviceRepository;

        @Autowired
        private FollowUpRepository followUpRepository;

        @Autowired
        private StudentRepository studentRepository;

        public DashboardResponseDTO getDashboard(UUID professionalId) {

                DashboardResponseDTO dto = new DashboardResponseDTO();

                if (professionalId == null) {
                        dto.setTotalAtendimentos(serviceRepository.count());
                        dto.setAtendimentosAgendados(serviceRepository.countByStatus("AGENDADO"));
                        dto.setAtendimentosRealizados(serviceRepository.countByStatus("REALIZADO"));
                        dto.setAtendimentosCancelados(serviceRepository.countByStatus("CANCELADO"));

                        dto.setTotalAcompanhamentos(followUpRepository.count());
                        dto.setAcompanhamentosAgendados(followUpRepository.countByStatus("AGENDADO"));
                        dto.setAcompanhamentosRealizados(followUpRepository.countByStatus("REALIZADO"));
                        dto.setAcompanhamentosCancelados(followUpRepository.countByStatus("CANCELADO"));
                } else {
                        dto.setTotalAtendimentos(serviceRepository.countByProfessionals_Id(professionalId));
                        dto.setAtendimentosAgendados(
                                        serviceRepository.countByProfessionals_IdAndStatus(professionalId, "AGENDADO"));
                        dto.setAtendimentosRealizados(
                                        serviceRepository.countByProfessionals_IdAndStatus(professionalId,
                                                        "REALIZADO"));
                        dto.setAtendimentosCancelados(
                                        serviceRepository.countByProfessionals_IdAndStatus(professionalId,
                                                        "CANCELADO"));

                        dto.setTotalAcompanhamentos(followUpRepository.countByProfessionals_Id(professionalId));
                        dto.setAcompanhamentosAgendados(
                                        followUpRepository.countByProfessionals_IdAndStatus(professionalId,
                                                        "AGENDADO"));
                        dto.setAcompanhamentosRealizados(
                                        followUpRepository.countByProfessionals_IdAndStatus(professionalId,
                                                        "REALIZADO"));
                        dto.setAcompanhamentosCancelados(
                                        followUpRepository.countByProfessionals_IdAndStatus(professionalId,
                                                        "CANCELADO"));
                }

                return dto;
        }

        public List<MonthlyDataDTO> getMonthlyEvolution(UUID professionalId) {
                List<MonthlyDataDTO> monthlyDataList = new ArrayList<>();

                Optional<Service> firstService = serviceRepository.findFirstByOrderBySessionDateAsc();
                Optional<FollowUp> firstFollowUp = followUpRepository.findFirstByOrderBySessionDateAsc();

                LocalDate overallStartDate = null;
                if (firstService.isPresent() && firstFollowUp.isPresent()) {
                        overallStartDate = firstService.get().getSessionDate()
                                        .isBefore(firstFollowUp.get().getSessionDate())
                                                        ? firstService.get().getSessionDate()
                                                        : firstFollowUp.get().getSessionDate();
                } else if (firstService.isPresent()) {
                        overallStartDate = firstService.get().getSessionDate();
                } else if (firstFollowUp.isPresent()) {
                        overallStartDate = firstFollowUp.get().getSessionDate();
                }

                if (overallStartDate == null) {
                        return monthlyDataList;
                }

                YearMonth startMonth = YearMonth.from(overallStartDate);
                YearMonth currentMonth = YearMonth.now();
                Locale ptBr = new Locale("pt", "BR");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/yyyy", ptBr);

                YearMonth monthIterator = startMonth;
                while (!monthIterator.isAfter(currentMonth)) {
                        LocalDate startDate = monthIterator.atDay(1);
                        LocalDate endDate = monthIterator.atEndOfMonth().plusDays(1);

                        long atendimentosCount;
                        long acompanhamentosCount;

                        if (professionalId == null) {
                                atendimentosCount = serviceRepository
                                                .count(ServiceSpecification.sessionDateIsIn(startDate, endDate));
                                acompanhamentosCount = followUpRepository
                                                .count(FollowUpSpecification.sessionDateIsIn(startDate, endDate));
                        } else {
                                atendimentosCount = serviceRepository.count(ServiceSpecification
                                                .byProfessionalAndDate(professionalId, startDate, endDate));
                                acompanhamentosCount = followUpRepository.count(FollowUpSpecification
                                                .byProfessionalAndDate(professionalId, startDate, endDate));
                        }

                        String monthLabel = monthIterator.format(formatter);
                        monthLabel = monthLabel.substring(0, 1).toUpperCase() + monthLabel.substring(1);
                        monthlyDataList.add(new MonthlyDataDTO(monthLabel, atendimentosCount, acompanhamentosCount));

                        monthIterator = monthIterator.plusMonths(1);
                }

                return monthlyDataList;
        }

        public List<StudentStatusDataDTO> getStudentStatusDistribution() {
                List<StudentStatusDataDTO> distribution = new ArrayList<>();

                long activeStudents = studentRepository.countByStatus("ATIVO");
                long inactiveStudents = studentRepository.countByStatus("INATIVO");

                distribution.add(new StudentStatusDataDTO("Ativos", activeStudents));
                distribution.add(new StudentStatusDataDTO("Inativos", inactiveStudents));

                return distribution;
        }

}
