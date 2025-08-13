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

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository; // <-- ADICIONADO

    // Injeção de dependências via construtor (melhor prática)
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder, ProfileRepository profileRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository; // <-- ADICIONADO
    }

    /**
     * Cria um novo estudante no sistema, validando dados únicos como login e matrícula.
     * (Baseado no Requisito Funcional: RF02) [cite_start][cite: 486, 628]
     * @param dto DTO com os dados para a criação do estudante.
     * @return DTO com os dados do estudante recém-criado, incluindo seu ID.
     */
    @Transactional
    public ReadStudentDTO createStudent(@Valid CreateStudentDTO dto) {
        // 1. Validação de duplicidade
        // O sistema deve verificar se já existe um estudante com a mesma matrícula [cite: 645]
        if (studentRepository.findByRegistration(dto.registration()).isPresent()) {
            throw new DataIntegrityViolationException("A matrícula '" + dto.registration() + "' já está cadastrada.");
        }
        // Embora não esteja explícito para login de estudante no RF02, é uma boa prática
        if (studentRepository.findByLogin(dto.login()).isPresent()) {
            throw new DataIntegrityViolationException("O login '" + dto.login() + "' já está em uso.");
        }

        // 2. Busca o perfil de ESTUDANTE no banco de dados
        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
            .orElseThrow(() -> new RuntimeException("Perfil ESTUDANTE não encontrado. Verifique se o DataSeeder foi executado."));

        // 3. Criação da nova entidade
        Student student = new Student();

        // 4. Mapeamento dos dados do DTO para a Entidade
        student.setLogin(dto.login());
        student.setPassword(passwordEncoder.encode(dto.password())); // Sempre codificar a senha!
        student.setProfile(studentProfile); // <-- ADICIONADO: Associa o perfil ao estudante
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        student.setTeam(dto.team());
        student.setBirthDate(dto.birthDate());
        student.setPhone(dto.phone());
        student.setEmail(dto.email());
        student.setGender(dto.gender());
        student.setEthnicity(dto.ethnicity());
        
        // 5. Definição de valores padrão (regra de negócio)
        student.setStatus("ATIVO");
        student.setActive(true);

        // 6. Persistência no banco de dados
        Student savedStudent = studentRepository.save(student);

        // 7. Retorno de um DTO com os dados do objeto salvo
        return new ReadStudentDTO(savedStudent);
    }
}