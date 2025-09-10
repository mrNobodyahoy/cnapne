package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.CreateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentSummaryDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.UpdateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Responsible;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataIntegrityViolationException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import jakarta.validation.Valid;

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

        LocalDate birthDate = dto.birthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18 && (dto.responsibles() == null || dto.responsibles().isEmpty())) {
            throw new IllegalArgumentException(
                    "Estudantes menores de 18 anos devem ter pelo menos um responsável cadastrado.");
        }

        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
                .orElseThrow(() -> new IllegalStateException("Perfil ESTUDANTE não encontrado."));

        Student student = new Student();
        student.setEmail(dto.email());
        student.setPassword(passwordEncoder.encode(dto.password()));
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
        student.setResponsibles(new ArrayList<>());

        if (dto.responsibles() != null && !dto.responsibles().isEmpty()) {
            dto.responsibles().forEach(respDto -> {
                Responsible responsible = new Responsible();
                responsible.setCompleteName(respDto.completeName());
                responsible.setEmail(respDto.email());
                responsible.setPhone(respDto.phone());
                responsible.setKinship(respDto.kinship());
                responsible.setStudent(student);
                student.getResponsibles().add(responsible);
            });
        }

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
    public ReadStudentDTO updateStudent(UUID id, @Valid UpdateStudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado com ID: " + id));

        userRepository.findByEmail(dto.email())
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("O email '" + dto.email() + "' já está em uso.");
                });

        student.setEmail(dto.email());
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        student.setTeam(dto.team());
        student.setBirthDate(dto.birthDate());
        student.setPhone(dto.phone());
        student.setGender(dto.gender());
        student.setEthnicity(dto.ethnicity());
        student.setStatus(dto.status());

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
    public List<ReadStudentSummaryDTO> findByStatus(String status) {
        List<Student> students = studentRepository.findByStatus(status);

        return students.stream()
                .map(s -> new ReadStudentSummaryDTO(
                        s.getId(),
                        s.getCompleteName(),
                        s.getRegistration(),
                        s.getTeam(),
                        s.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReadStudentSummaryDTO> search(String query) {
        final String isNumericRegex = "^\\d+$";

        List<Student> students;
        if (query.matches(isNumericRegex)) {
            students = studentRepository.findByRegistrationContainingIgnoreCase(query);
        } else {
            students = studentRepository.findByCompleteNameContainingIgnoreCase(query);
        }

        return students.stream()
            .map(s -> new ReadStudentSummaryDTO(
                    s.getId(),
                    s.getCompleteName(),
                    s.getRegistration(),
                    s.getTeam(),
                    s.getStatus())) 
            .collect(Collectors.toList());
    }
}
