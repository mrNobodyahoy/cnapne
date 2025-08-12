package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.CreateStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.student.ReadStudentDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.repository.StudentRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.DataIntegrityViolationException;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependências via construtor
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo estudante no sistema, validando dados únicos como login e matrícula.
     * (Baseado no Requisito Funcional: RF02)
     * @param dto DTO com os dados para a criação do estudante.
     * @return DTO com os dados do estudante recém-criado, incluindo seu ID.
     */
    @Transactional
    public ReadStudentDTO createStudent(@Valid CreateStudentDTO dto) {
        // 1. Validação de duplicidade (semelhante ao exemplo que verifica o CPF)
        if (studentRepository.findByLogin(dto.login()).isPresent()) {
            throw new DataIntegrityViolationException("O login '" + dto.login() + "' já está em uso.");
        }
        if (studentRepository.findByRegistration(dto.registration()).isPresent()) {
            throw new DataIntegrityViolationException("A matrícula '" + dto.registration() + "' já está cadastrada.");
        }

        // 2. Criação da nova entidade
        Student student = new Student();

        // 3. Mapeamento dos dados do DTO para a Entidade
        student.setLogin(dto.login());
        student.setPassword(passwordEncoder.encode(dto.password())); // Sempre codificar a senha!
        student.setCompleteName(dto.completeName());
        student.setRegistration(dto.registration());
        student.setTeam(dto.team());
        student.setBirthDate(dto.birthDate());
        student.setPhone(dto.phone());
        student.setEmail(dto.email());
        student.setGender(dto.gender());
        student.setEthnicity(dto.ethnicity());
        
        // 4. Definição de valores padrão (regra de negócio)
        student.setStatus("ATIVO");
        student.setActive(true);

        // 5. Persistência no banco de dados
        Student savedStudent = studentRepository.save(student);

        // 6. Retorno de um DTO com os dados do objeto salvo
        return new ReadStudentDTO(savedStudent);
    }

}