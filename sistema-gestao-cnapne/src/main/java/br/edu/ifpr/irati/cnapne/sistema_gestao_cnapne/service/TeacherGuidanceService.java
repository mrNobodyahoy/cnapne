package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.CreateTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.ReadTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.teacherGuidance.UpdateTeacherGuidanceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.TeacherGuidance;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.ResourceNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.FollowUpRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfessionalRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ServiceRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.TeacherGuidanceRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import jakarta.validation.Valid;

@org.springframework.stereotype.Service
public class TeacherGuidanceService {

    @Autowired
    private TeacherGuidanceRepository guidanceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ReadTeacherGuidanceDTO create(@Valid CreateTeacherGuidanceDTO createDto) {

        Service service = null;
        FollowUp followUp = null;

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User appUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado."));

        Professional author = professionalRepository.findById(appUser.getId())
                .orElseThrow(() -> new IllegalStateException("O usuário logado não é um profissional."));

        if (createDto.getServiceId() != null) {
            service = serviceRepository.findById(createDto.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Atendimento não encontrado com ID: " + createDto.getServiceId()));
        }

        if (createDto.getFollowUpId() != null) {
            followUp = followUpRepository.findById(createDto.getFollowUpId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Acompanhamento não encontrado com ID: " + createDto.getFollowUpId()));
        }

        if (service != null && followUp != null &&
                !service.getStudent().getId().equals(followUp.getStudent().getId())) {
            throw new IllegalArgumentException("O Atendimento e o Acompanhamento pertencem a estudantes diferentes.");
        }

        if ((service != null && service.getTeacherGuidance() != null)
                || (followUp != null && followUp.getTeacherGuidance() != null)) {
            throw new IllegalStateException("Já existe uma orientação para este atendimento ou acompanhamento.");
        }

        if (service == null && followUp == null) {
            throw new IllegalArgumentException("É necessário vincular ao menos um Atendimento ou Acompanhamento.");
        }

        TeacherGuidance guidance = new TeacherGuidance();
        guidance.setStudent(service != null ? service.getStudent() : followUp.getStudent());
        guidance.setDomiciliar(createDto.getDomiciliar());
        guidance.setGuidanceDetails(createDto.getGuidanceDetails());
        guidance.setRecommendations(createDto.getRecommendations());

        guidance.setAuthor(author);
        if (service != null) {
            service.setTeacherGuidance(guidance);
            guidance.setService(service);
        }
        if (followUp != null) {
            followUp.setTeacherGuidance(guidance);
            guidance.setFollowUp(followUp);
        }

        TeacherGuidance savedGuidance = null;

        if (service != null) {
            savedGuidance = serviceRepository.save(service).getTeacherGuidance();
        }

        if (followUp != null) {
            FollowUp savedFollowUp = followUpRepository.save(followUp);
            if (savedGuidance == null) {
                savedGuidance = savedFollowUp.getTeacherGuidance();
            }
        }

        return new ReadTeacherGuidanceDTO(savedGuidance);
    }

    @Transactional(readOnly = true)
    public ReadTeacherGuidanceDTO getById(UUID id) {
        TeacherGuidance guidance = guidanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orientação não encontrada com ID: " + id));
        return new ReadTeacherGuidanceDTO(guidance);
    }

    @Transactional
    public ReadTeacherGuidanceDTO update(UUID id, @Valid UpdateTeacherGuidanceDTO updateDto) {
        TeacherGuidance existingGuidance = guidanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orientação não encontrada com ID: " + id));

        existingGuidance.setDomiciliar(updateDto.getDomiciliar());
        existingGuidance.setGuidanceDetails(updateDto.getGuidanceDetails());
        existingGuidance.setRecommendations(updateDto.getRecommendations());

        TeacherGuidance updatedGuidance = guidanceRepository.save(existingGuidance);
        return new ReadTeacherGuidanceDTO(updatedGuidance);
    }

    @Transactional
    public void delete(UUID id) {
        TeacherGuidance guidance = guidanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orientação não encontrada com ID: " + id));

        Service service = guidance.getService();
        FollowUp followUp = guidance.getFollowUp();

        if (service != null) {
            service.setTeacherGuidance(null);
            serviceRepository.save(service);
        }

        if (followUp != null) {
            followUp.setTeacherGuidance(null);
            followUpRepository.save(followUp);
        }

        guidanceRepository.delete(guidance);
    }

    @Transactional(readOnly = true)
    public Page<ReadTeacherGuidanceDTO> findAllPaginated(
            String studentName,
            Boolean domiciliar,
            Pageable pageable,
            User authenticatedUser) {

        Specification<TeacherGuidance> spec = TeacherGuidanceSpecification.studentNameContains(studentName);

        spec = spec.and(TeacherGuidanceSpecification.domiciliarEquals(domiciliar));

        if (authenticatedUser.getProfile().getName() != Role.COORDENACAO_CNAPNE) {
            spec = spec.and(TeacherGuidanceSpecification.authoredBy(authenticatedUser.getId()));
        }

        return guidanceRepository.findAll(spec, pageable).map(ReadTeacherGuidanceDTO::new);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado com o email: " + userEmail));
    }
}
