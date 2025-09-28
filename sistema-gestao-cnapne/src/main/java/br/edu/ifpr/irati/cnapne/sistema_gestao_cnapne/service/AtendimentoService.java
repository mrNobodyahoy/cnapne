package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.CreateServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.ReadServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.ServiceResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Session.atendimentoService.UpdateServiceDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.ResourceNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfessionalRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ServiceRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import jakarta.validation.Valid;

@org.springframework.stereotype.Service
public class AtendimentoService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;

    @Transactional
    public ServiceResponseDTO create(CreateServiceDTO createDto) {
        Student student = studentRepository.findById(createDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + createDto.getStudentId()));

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

        // ADICIONADO: Mapeamento dos novos campos na resposta
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

        return responseDTO;
    }

    @Transactional(readOnly = true)
    public ReadServiceDTO getServiceById(UUID id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado com ID: " + id));
        return new ReadServiceDTO(service);
    }

    @Transactional
    public ReadServiceDTO update(UUID id, @Valid UpdateServiceDTO updateDto) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado com ID: " + id));

        Student student = studentRepository.findById(updateDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudante não encontrado com ID: " + updateDto.getStudentId()));

        List<Professional> newProfessionals = professionalRepository.findAllById(updateDto.getProfessionalIds());
        if (newProfessionals.size() != updateDto.getProfessionalIds().size()) {
            throw new ResourceNotFoundException("Um ou mais profissionais não encontrados com os IDs fornecidos.");
        }

        existingService.setSessionDate(updateDto.getSessionDate());
        existingService.setSessionTime(updateDto.getSessionTime());
        existingService.setSessionLocation(updateDto.getSessionLocation());
        existingService.setStatus(updateDto.getStatus());
        existingService.setTypeService(updateDto.getTypeService());
        existingService.setDescriptionService(updateDto.getDescriptionService());
        existingService.setTasks(updateDto.getTasks());
        existingService.setObjectives(updateDto.getObjectives());
        existingService.setResults(updateDto.getResults());
        existingService.setStudent(student);

        for (Professional prof : existingService.getProfessionals()) {
            prof.getServices().remove(existingService);
        }
        existingService.getProfessionals().clear();

        for (Professional prof : newProfessionals) {
            prof.getServices().add(existingService);
            existingService.getProfessionals().add(prof);
        }

        Service updatedService = serviceRepository.save(existingService);

        return new ReadServiceDTO(updatedService);
    }

    @Transactional(readOnly = true)
    public Page<ReadServiceDTO> findAllPaginated(String studentName, String status, Pageable pageable) {
        Specification<Service> spec = ServiceSpecification.studentNameContains(studentName)
                .and(ServiceSpecification.hasStatus(status));

        return serviceRepository.findAll(spec, pageable).map(ReadServiceDTO::new);
    }

    @Transactional
    public void delete(UUID id) {

        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado com ID: " + id));

        for (Professional prof : existingService.getProfessionals()) {
            prof.getServices().remove(existingService);
        }

        serviceRepository.delete(existingService);
    }

}