package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.CreateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.UpdateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.ChangePasswordDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataIntegrityViolationException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfessionalRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import jakarta.validation.Valid;

@Service
public class ProfessionalService {

    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfessionalService(UserRepository userRepository,
            ProfessionalRepository professionalRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ReadProfessionalDTO createProfessional(CreateProfessionalDTO dto) {
        userRepository.findByEmail(dto.email()).ifPresent(user -> {
            throw new DataIntegrityViolationException("O e-mail já está em uso.");
        });

        Profile profile = profileRepository.findByName(Role.valueOf(dto.role()))
                .orElseThrow(() -> new DataNotFoundException("Perfil " + dto.role() + " não encontrado."));

        Professional professional = new Professional();
        professional.setEmail(dto.email());
        professional.setPassword(passwordEncoder.encode(dto.password()));
        professional.setActive(true);
        professional.setProfile(profile);
        professional.setFullName(dto.fullName());
        professional.setSpecialty(dto.specialty());

        Professional saved = professionalRepository.save(professional);
        return new ReadProfessionalDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReadProfessionalDTO> findAllPaginatedAndFiltered(
            String query, Boolean active, Role role, Pageable pageable) {

        Specification<Professional> spec = ProfessionalSpecification.hasFullName(query)
                .and(ProfessionalSpecification.isActive(active))
                .and(ProfessionalSpecification.hasRole(role));

        Page<Professional> professionalPage = professionalRepository.findAll(spec, pageable);

        return professionalPage.map(ReadProfessionalDTO::new);
    }

    @Transactional(readOnly = true)
    public ReadProfessionalDTO getProfessionalById(UUID id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Profissional não encontrado com ID: " + id));
        return new ReadProfessionalDTO(professional);
    }

    @Transactional
    public ReadProfessionalDTO updateProfessional(UUID id, @Valid UpdateProfessionalDTO dto) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Profissional não encontrado com ID: " + id));

        userRepository.findByEmail(dto.email())
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("O e-mail informado já está em uso.");
                });

        Profile profile = profileRepository.findByName(Role.valueOf(dto.role()))
                .orElseThrow(() -> new DataNotFoundException("Perfil '" + dto.role() + "' não encontrado."));

        professional.setEmail(dto.email());
        professional.setFullName(dto.fullName());
        professional.setSpecialty(dto.specialty());
        professional.setProfile(profile);
        professional.setActive(dto.active());

        Professional updatedProfessional = professionalRepository.save(professional);
        return new ReadProfessionalDTO(updatedProfessional);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordDTO dto) {
        Professional professional = professionalRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Profissional não encontrado."));

        if (!passwordEncoder.matches(dto.currentPassword(), professional.getPassword())) {
            throw new BadCredentialsException("A senha atual está incorreta.");
        }

        professional.setPassword(passwordEncoder.encode(dto.newPassword()));
        professionalRepository.save(professional);
    }

    @Transactional
    public void deleteProfessional(UUID id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Profissional não encontrado com ID: " + id));
        professionalRepository.delete(professional);
    }
}