package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.CreateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataIntegrityViolationException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    public StudentService(StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            ProfileRepository profileRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ReadStudentDTO createStudent(@Valid CreateStudentDTO dto) {
        studentRepository.findByRegistration(dto.registration()).ifPresent(s -> {
            throw new DataIntegrityViolationException(
                    "A matrícula '" + dto.registration() + "' já está cadastrada.");
        });

        studentRepository.findByLogin(dto.login()).ifPresent(s -> {
            throw new DataIntegrityViolationException(
                    "O login '" + dto.login() + "' já está em uso.");
        });

        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
                .orElseThrow(() -> new IllegalStateException(
                        "Perfil ESTUDANTE não encontrado. Verifique se o DataSeeder foi executado."));

        Student student = new Student();
        student.setLogin(dto.login());
        student.setPassword(passwordEncoder.encode(dto.password())); 
        student.setProfile(studentProfile);
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        student.setTeam(dto.team());
        student.setBirthDate(dto.birthDate());
        student.setPhone(dto.phone());
        student.setEmail(dto.email());
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

        studentRepository.findByRegistration(dto.registration())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new DataIntegrityViolationException(
                            "A matrícula '" + dto.registration() + "' já está em uso.");
                });

        studentRepository.findByLogin(dto.login())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new DataIntegrityViolationException("O login '" + dto.login() + "' já está em uso.");
                });

        student.setLogin(dto.login());
        student.setPassword(passwordEncoder.encode(dto.password()));
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        student.setTeam(dto.team());
        student.setBirthDate(dto.birthDate());
        student.setPhone(dto.phone());
        student.setEmail(dto.email());
        student.setGender(dto.gender());
        student.setEthnicity(dto.ethnicity());

        Student updated = studentRepository.save(student);

        return new ReadStudentDTO(updated);
    }

    @Transactional
    public void deleteStudent(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado com ID: " + id));
        studentRepository.delete(student);
    }
}
