package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.CreateServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.ReadServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.ServiceResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.UpdateServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.EmailDto;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.ResourceNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfessionalRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ServiceRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import jakarta.validation.Valid;

@org.springframework.stereotype.Service
public class AtendimentoService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailPublisherService emailPublisherService;

    @Transactional
    public ServiceResponseDTO create(CreateServiceDTO createDto) {
        Student student = studentRepository.findById(createDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + createDto.getStudentId()));

        if (createDto.getSessionDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data do atendimento não pode ser anterior à data de hoje.");
        }

        List<Professional> professionals = professionalRepository.findAllById(createDto.getProfessionalIds());
        if (professionals.size() != createDto.getProfessionalIds().size()) {
            throw new ResourceNotFoundException("Um ou mais profissionais não encontrados com os IDs fornecidos.");
        }

        Service newService = new Service();
        newService.setSessionDate(createDto.getSessionDate());
        newService.setSessionTime(createDto.getSessionTime());
        newService.setSessionLocation(createDto.getSessionLocation());
        newService.setStatus("AGENDADO");
        newService.setTypeService(createDto.getTypeService());
        newService.setDescriptionService(createDto.getDescriptionService());
        newService.setTasks(createDto.getTasks());
        newService.setObjectives(createDto.getObjectives());
        newService.setStudent(student);
        newService.setProfessionals(professionals);

        for (Professional prof : professionals) {
            prof.getServices().add(newService);
        }

        Service savedService = serviceRepository.save(newService);

        ServiceResponseDTO responseDTO = new ServiceResponseDTO();
        responseDTO.setSessionId(savedService.getSessionId());
        responseDTO.setCreatedAt(savedService.getCreatedAt());
        responseDTO.setSessionDate(savedService.getSessionDate());
        responseDTO.setSessionTime(savedService.getSessionTime());
        responseDTO.setSessionLocation(savedService.getSessionLocation());
        responseDTO.setStatus(savedService.getStatus());
        responseDTO.setTypeService(savedService.getTypeService());
        responseDTO.setDescriptionService(savedService.getDescriptionService());
        responseDTO.setTasks(savedService.getTasks());

        responseDTO.setObjectives(savedService.getObjectives());

        ReadStudentSummaryDTO studentDto = new ReadStudentSummaryDTO(
                savedService.getStudent().getId(),
                savedService.getStudent().getCompleteName(),
                savedService.getStudent().getRegistration(),
                savedService.getStudent().getTeam(),
                savedService.getStudent().getStatus());
        responseDTO.setStudent(studentDto);

        List<ReadProfessionalDTO> professionalDTOs = savedService.getProfessionals().stream()
                .map(ReadProfessionalDTO::new)
                .collect(Collectors.toList());
        responseDTO.setProfessionals(professionalDTOs);
        sendServiceNotificationEmail(savedService, "agendado");

        return responseDTO;
    }

    @Transactional(readOnly = true)
    public ReadServiceDTO getServiceById(UUID id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado com ID: " + id));

        User authenticatedUser = getAuthenticatedUser(); // Usa o novo método
        if (authenticatedUser.getProfile().getName() == Role.ESTUDANTE) {
            if (!service.getStudent().getId().equals(authenticatedUser.getId())) {
                throw new AccessDeniedException("Acesso negado.");
            }
        }

        return new ReadServiceDTO(service);
    }

    @Transactional
    public ReadServiceDTO update(UUID id, @Valid UpdateServiceDTO updateDto) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado com ID: " + id));

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getProfile().getName() == Role.ESTUDANTE) {
            if (!existingService.getStudent().getId().equals(authenticatedUser.getId())) {
                throw new AccessDeniedException("Acesso negado.");
            }
        }

        Student student = studentRepository.findById(updateDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + updateDto.getStudentId()));

        List<Professional> newProfessionals = professionalRepository.findAllById(updateDto.getProfessionalIds());
        if (newProfessionals.size() != updateDto.getProfessionalIds().size()) {
            throw new ResourceNotFoundException("Um ou mais profissionais não encontrados.");
        }

        existingService.setSessionDate(updateDto.getSessionDate());
        existingService.setSessionTime(updateDto.getSessionTime());
        existingService.setSessionLocation(updateDto.getSessionLocation());
        existingService.setStatus(updateDto.getStatus());
        existingService.setTypeService(updateDto.getTypeService());
        existingService.setDescriptionService(updateDto.getDescriptionService());
        existingService.setStudent(student);

        existingService.setObjectives(updateDto.getObjectives());
        existingService.setResults(updateDto.getResults());
        existingService.setTasks(updateDto.getTasks());

        existingService.getProfessionals().forEach(prof -> prof.getServices().remove(existingService));
        existingService.getProfessionals().clear();

        newProfessionals.forEach(prof -> {
            prof.getServices().add(existingService);
            existingService.getProfessionals().add(prof);
        });

        Service updatedService = serviceRepository.save(existingService);

        String actionType = "atualizado";
        if ("REALIZADO".equalsIgnoreCase(updatedService.getStatus())) {
            actionType = "marcado como realizado";
        } else if ("CANCELADO".equalsIgnoreCase(updatedService.getStatus())) {
            actionType = "cancelado";
        }

        sendServiceNotificationEmail(updatedService, actionType);
        return new ReadServiceDTO(updatedService);
    }

    @Transactional(readOnly = true)
    public Page<ReadServiceDTO> findAllPaginated(String studentName, String status, Pageable pageable,
            User authenticatedUser) {
        Specification<Service> spec = ServiceSpecification.studentNameContains(studentName)
                .and(ServiceSpecification.hasStatus(status));

        if (authenticatedUser.getProfile().getName() != Role.COORDENACAO_CNAPNE) {
            spec = spec.and(ServiceSpecification.hasProfessional(authenticatedUser.getId()));
        }

        return serviceRepository.findAll(spec, pageable).map(ReadServiceDTO::new);
    }

    @Transactional
    public void delete(UUID id) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado com ID: " + id));

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getProfile().getName() == Role.ESTUDANTE) {
            if (!existingService.getStudent().getId().equals(authenticatedUser.getId())) {
                throw new AccessDeniedException("Acesso negado.");
            }

        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + userEmail));
    }

    private void sendServiceNotificationEmail(Service service, String actionType) {
        Student student = service.getStudent();
        List<Professional> professionals = service.getProfessionals();
        String templatePath = "email/notificacao-atendimento";
        String subject = "Atendimento " + actionType + ": " + student.getName();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("studentName", student.getName());
        emailProps.put("actionType", actionType);
        emailProps.put("sessionDate", service.getSessionDate().format(dateFormatter)); // Formata a data
        emailProps.put("sessionTime", service.getSessionTime().toString().substring(0, 5)); // Formata a hora para HH:mm
        emailProps.put("sessionLocation", service.getSessionLocation());
        emailProps.put("sessionStatus", service.getStatus()); // Adiciona o status

        emailProps.put("recipientName", student.getName());
        EmailDto studentEmail = new EmailDto(student.getEmail(), subject, templatePath, new HashMap<>(emailProps));
        emailPublisherService.scheduleEmail(studentEmail);

        for (Professional prof : professionals) {
            emailProps.put("recipientName", prof.getName());
            EmailDto profEmail = new EmailDto(prof.getEmail(), subject, templatePath, new HashMap<>(emailProps));
            emailPublisherService.scheduleEmail(profEmail);
        }
    }
}