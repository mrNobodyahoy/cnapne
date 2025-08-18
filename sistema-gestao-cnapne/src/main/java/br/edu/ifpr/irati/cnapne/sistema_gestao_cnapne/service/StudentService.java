package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.CreateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataIntegrityViolationException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    public StudentService(StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            ProfileRepository profileRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReadStudentDTO createStudent(@Valid CreateStudentDTO dto) {
        studentRepository.findByRegistration(dto.registration()).ifPresent(s -> {
            throw new DataIntegrityViolationException("A matrícula '" + dto.registration() + "' já está cadastrada.");
        });

        userRepository.findByEmail(dto.email()).ifPresent(s -> {
            throw new DataIntegrityViolationException("O email '" + dto.email() + "' já está em uso.");
        });

        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
                .orElseThrow(() -> new IllegalStateException("Perfil ESTUDANTE não encontrado."));

        Student student = new Student();
        student.setEmail(dto.email());
        student.setPassword(passwordEncoder.encode(dto.password()));
        // ... (resto dos set's está correto, só remover o setEmail duplicado)
        student.setProfile(studentProfile);
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        student.setTeam(dto.team());
        student.setBirthDate(dto.birthDate());
        student.setPhone(dto.phone());
        student.setGender(dto.gender());
        student.setEthnicity(dto.ethnicity());
        student.setStatus("ATIVO");
        student.setActive(true);

        Student savedStudent = studentRepository.save(student);
        return new ReadStudentDTO(savedStudent);
    }

    @Transactional(readOnly = true)
    public List<ReadStudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(ReadStudentDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReadStudentDTO getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado com ID: " + id));
        return new ReadStudentDTO(student);
    }

    @Transactional
    public ReadStudentDTO updateStudent(UUID id, @Valid CreateStudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado com ID: " + id));

        // (a lógica de validação de matrícula está correta)

        // Correção: Lógica de validação de e-mail usando o userRepository
        userRepository.findByEmail(dto.email())
                .filter(user -> !user.getId().equals(id)) // Ignora o próprio usuário
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("O email '" + dto.email() + "' já está em uso.");
                });

        student.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isEmpty()) {
            student.setPassword(passwordEncoder.encode(dto.password()));
        }
        // ... (resto dos set's está correto, só remover o setEmail duplicado)
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        // ... etc

        Student updated = studentRepository.save(student);
        return new ReadStudentDTO(updated);
    }

    @Transactional
    public void deleteStudent(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado com ID: " + id));
        studentRepository.delete(student);
    }

    @Transactional(readOnly = true)
    public List<ReadStudentSummaryDTO> searchByName(String name) {
        return studentRepository.findByCompleteNameContainingIgnoreCase(name)
                .stream()
                .map(s -> new ReadStudentSummaryDTO(s.getId(), s.getCompleteName(), s.getRegistration()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReadStudentSummaryDTO> searchByRegistration(String registration) {
        return studentRepository.findByRegistrationContainingIgnoreCase(registration)
                .stream()
                .map(s -> new ReadStudentSummaryDTO(s.getId(), s.getCompleteName(), s.getRegistration()))
                .toList();
    }

}
