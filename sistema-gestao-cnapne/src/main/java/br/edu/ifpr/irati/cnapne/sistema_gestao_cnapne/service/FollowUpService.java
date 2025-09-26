package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.CreateFollowUpDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.FollowUpResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.ReadFollowUpDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.followUp.UpdateFollowUpDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.ResourceNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.FollowUpRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfessionalRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import jakarta.validation.Valid;

@Service
public class FollowUpService {
    @Autowired
    private FollowUpRepository followUpRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;

    @Transactional
    public FollowUpResponseDTO create(CreateFollowUpDTO createDto) {
        Student student = studentRepository.findById(createDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + createDto.getStudentId()));

        List<Professional> professionals = professionalRepository.findAllById(createDto.getProfessionalIds());
        if (professionals.size() != createDto.getProfessionalIds().size()) {
            throw new ResourceNotFoundException("Um ou mais profissionais não encontrados com os IDs fornecidos.");
        }

        FollowUp newFollowUp = new FollowUp();
        newFollowUp.setSessionDate(createDto.getSessionDate());
        newFollowUp.setSessionTime(createDto.getSessionTime());
        newFollowUp.setSessionLocation(createDto.getSessionLocation());
        newFollowUp.setStatus(createDto.getStatus());
        newFollowUp.setDescription(createDto.getDescription());
        newFollowUp.setTasks(createDto.getTasks());
        newFollowUp.setStudent(student);
        newFollowUp.setProfessionals(professionals);

        // ADICIONADO: Mapeamento dos novos campos
        newFollowUp.setAreasCovered(createDto.getAreasCovered());

        for (Professional prof : professionals) {
            prof.getFollowUps().add(newFollowUp);
        }

        FollowUp savedFollowUp = followUpRepository.save(newFollowUp);

        FollowUpResponseDTO responseDTO = new FollowUpResponseDTO();
        responseDTO.setSessionId(savedFollowUp.getSessionId());
        responseDTO.setCreatedAt(savedFollowUp.getCreatedAt());
        responseDTO.setSessionDate(savedFollowUp.getSessionDate());
        responseDTO.setSessionTime(savedFollowUp.getSessionTime());
        responseDTO.setSessionLocation(savedFollowUp.getSessionLocation());
        responseDTO.setStatus(savedFollowUp.getStatus());
        responseDTO.setDescription(savedFollowUp.getDescription());
        responseDTO.setTasks(savedFollowUp.getTasks());

        // ADICIONADO: Mapeamento dos novos campos na resposta
        responseDTO.setAreasCovered(savedFollowUp.getAreasCovered());

        ReadStudentSummaryDTO studentDto = new ReadStudentSummaryDTO(
                savedFollowUp.getStudent().getId(),
                savedFollowUp.getStudent().getCompleteName(),
                savedFollowUp.getStudent().getRegistration(),
                savedFollowUp.getStudent().getTeam(),
                savedFollowUp.getStudent().getStatus());
        responseDTO.setStudent(studentDto);

        List<ReadProfessionalDTO> professionalDTOs = savedFollowUp.getProfessionals().stream()
                .map(ReadProfessionalDTO::new)
                .collect(Collectors.toList());
        responseDTO.setProfessionals(professionalDTOs);

        return responseDTO;
    }

    @Transactional(readOnly = true)
    public ReadFollowUpDTO getFollowUpById(UUID id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado com ID: " + id));

        return new ReadFollowUpDTO(followUp);
    }

    @Transactional
    public ReadFollowUpDTO update(UUID id, @Valid UpdateFollowUpDTO updateDto) {
        FollowUp existingFollowUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado com ID: " + id));

        Student student = studentRepository.findById(updateDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + updateDto.getStudentId()));

        List<Professional> newProfessionals = professionalRepository.findAllById(updateDto.getProfessionalIds());
        if (newProfessionals.size() != updateDto.getProfessionalIds().size()) {
            throw new ResourceNotFoundException("Um ou mais profissionais não encontrados com os IDs fornecidos.");
        }

        existingFollowUp.setSessionDate(updateDto.getSessionDate());
        existingFollowUp.setSessionTime(updateDto.getSessionTime());
        existingFollowUp.setSessionLocation(updateDto.getSessionLocation());
        existingFollowUp.setStatus(updateDto.getStatus());
        existingFollowUp.setTasks(updateDto.getTasks());
        existingFollowUp.setStudent(student);
        existingFollowUp.setDescription(updateDto.getDescription());

        // ADICIONADO: Mapeamento dos novos campos
        existingFollowUp.setAreasCovered(updateDto.getAreasCovered());
        existingFollowUp.setNextSteps(updateDto.getNextSteps());

        for (Professional prof : existingFollowUp.getProfessionals()) {
            prof.getFollowUps().remove(existingFollowUp);
        }
        existingFollowUp.getProfessionals().clear();

        for (Professional prof : newProfessionals) {
            prof.getFollowUps().add(existingFollowUp);
            existingFollowUp.getProfessionals().add(prof);
        }
        FollowUp updatedFollowUp = followUpRepository.save(existingFollowUp);

        return new ReadFollowUpDTO(updatedFollowUp);
    }

    @Transactional
    public void delete(UUID id) {

        FollowUp existingFollowUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado com ID: " + id));

        for (Professional prof : existingFollowUp.getProfessionals()) {
            prof.getFollowUps().remove(existingFollowUp);
        }

        followUpRepository.delete(existingFollowUp);
    }

    @Transactional(readOnly = true)
    public Page<ReadFollowUpDTO> findAllPaginated(UUID studentId, UUID professionalId, String status,
            LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<FollowUp> spec = Specification
                .where(FollowUpSpecification.hasStudent(studentId))
                .and(FollowUpSpecification.hasProfessional(professionalId))
                .and(FollowUpSpecification.hasStatus(status))
                .and(FollowUpSpecification.sessionDateIsBetween(startDate, endDate));

        return followUpRepository.findAll(spec, pageable).map(ReadFollowUpDTO::new);
    }
}