package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.CreateProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.ReadProfessionalDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.Professional.UpdateProfessionalDTO;
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
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new DataIntegrityViolationException("O e-mail já está em uso.");
        }

        Profile profile = profileRepository.findByName(Role.valueOf(dto.role()))
                .orElseThrow(() -> new RuntimeException("Perfil " + dto.role() + " não encontrado."));

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

    // --- LÓGICA DE BUSCA E FILTRO ALTERADA ---

    /**
     * Retorna todos os profissionais sem filtro.
     */
    @Transactional(readOnly = true)
    public List<ReadProfessionalDTO> getAllProfessionals() {
        return professionalRepository.findAll()
                .stream()
                .map(ReadProfessionalDTO::new)
                .toList();
    }

    /**
     * Lógica de busca principal, similar ao search() de StudentService.
     * Busca profissionais pelo nome completo.
     */
    @Transactional(readOnly = true)
    public List<ReadProfessionalDTO> search(String query) {
        List<Professional> professionals = professionalRepository.findByFullNameContainingIgnoreCase(query);
        return professionals.stream()
                .map(ReadProfessionalDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lógica de filtro por status, similar ao findByStatus() de StudentService.
     */
    @Transactional(readOnly = true)
    public List<ReadProfessionalDTO> findByActive(boolean active) {
        List<Professional> professionals = professionalRepository.findByActive(active);
        return professionals.stream()
                .map(ReadProfessionalDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lógica de filtro por perfil/cargo.
     */
    @Transactional(readOnly = true)
    public List<ReadProfessionalDTO> findByRole(Role role) {
        List<Professional> professionals = professionalRepository.findByProfileName(role);
        return professionals.stream()
                .map(ReadProfessionalDTO::new)
                .collect(Collectors.toList());
    }
    
    // --- FIM DA LÓGICA ALTERADA ---


    @Transactional(readOnly = true)
    public ReadProfessionalDTO getProfessionalById(UUID id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com ID: " + id));
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
    public void deleteProfessional(UUID id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com ID: " + id));
        professionalRepository.delete(professional);
    }

}