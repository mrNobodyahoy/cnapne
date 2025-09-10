package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Responsible;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependência via construtor (melhor prática)
    public DataSeeder(ProfileRepository profileRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- CRIAÇÃO DE PERFIS (ROLES) ---
        Arrays.stream(Role.values()).forEach(roleEnum -> {
            if (profileRepository.findByName(roleEnum).isEmpty()) {
                Profile profile = new Profile();
                profile.setName(roleEnum);
                profileRepository.save(profile);
                System.out.println("Perfil criado: " + roleEnum.name());
            }
        });

        // --- CRIAÇÃO DO USUÁRIO COORDENADOR PADRÃO ---
        if (userRepository.findByEmail("coordenador@ifpr.edu.br").isEmpty()) {
            Profile coordProfile = profileRepository.findByName(Role.COORDENACAO_CNAPNE)
                .orElseThrow(() -> new RuntimeException("Perfil COORDENACAO_CNAPNE não encontrado."));

            Professional coord = new Professional();
            coord.setEmail("coordenador@ifpr.edu.br");
            coord.setPassword(passwordEncoder.encode("admin123"));
            coord.setProfile(coordProfile);
            coord.setActive(true);
            coord.setFullName("Coordenador Padrão");
            coord.setSpecialty("Coordenação CNAPNE");

            userRepository.save(coord);
            System.out.println("Usuário 'coordenador' padrão criado com sucesso.");
        }

        // --- POPULAÇÃO DE ESTUDANTES (20) ---
        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
            .orElseThrow(() -> new RuntimeException("Perfil ESTUDANTE não encontrado."));

        for (int i = 1; i <= 20; i++) {
            String email = "aluno" + i + "@ifpr.edu.br";
            if (userRepository.findByEmail(email).isEmpty()) {
                Student student = new Student();
                student.setEmail(email);
                student.setPassword(passwordEncoder.encode("aluno123"));
                student.setActive(i % 10 != 0); // Torna 2 estudantes inativos
                student.setProfile(studentProfile);
                student.setCompleteName("Aluno " + i);
                student.setRegistration("2024" + String.format("%04d", i));
                student.setTeam("TURMA" + ((i % 4) + 1));
                student.setBirthDate(Date.from(
                        LocalDate.of(2005 + (i % 5), (i % 12) + 1, (i % 28) + 1)
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                student.setPhone("4299" + (100000 + i));
                student.setGender(i % 2 == 0 ? "Masculino" : "Feminino");
                student.setEthnicity(i % 3 == 0 ? "Branca" : "Parda");
                student.setStatus(student.isActive() ? "ATIVO" : "INATIVO");

                // Adiciona responsável se for menor de 18 (cálculo corrigido)
                LocalDate birthDate = student.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                if (age < 18) {
                    Responsible resp = new Responsible();
                    resp.setCompleteName("Responsável do Aluno " + i);
                    resp.setEmail("responsavel" + i + "@gmail.com");
                    resp.setPhone("4298" + (200000 + i));
                    resp.setKinship("Pai/Mãe");
                    resp.setStudent(student);
                    student.setResponsibles(List.of(resp));
                }

                userRepository.save(student);
                System.out.println("Estudante '" + student.getCompleteName() + "' criado.");
            }
        }

        // --- POPULAÇÃO DE PROFISSIONAIS (20) ---
        List<Role> professionalRoles = List.of(Role.EQUIPE_ACOMPANHAMENTO, Role.EQUIPE_AEE);
        List<String> specialties = List.of("Psicologia", "Pedagogia", "Assistência Social", "Outros");

        for (int i = 1; i <= 20; i++) {
            String email = "profissional" + i + "@ifpr.edu.br";
            if (userRepository.findByEmail(email).isEmpty()) {
                Role role = professionalRoles.get(i % professionalRoles.size());
                Profile profProfile = profileRepository.findByName(role)
                    .orElseThrow(() -> new RuntimeException("Perfil " + role + " não encontrado."));

                Professional professional = new Professional();
                professional.setEmail(email);
                professional.setPassword(passwordEncoder.encode("profissional123"));
                professional.setActive(i % 10 != 0); // Torna 2 profissionais inativos
                professional.setProfile(profProfile);
                professional.setFullName("Profissional " + i);
                professional.setSpecialty(specialties.get(i % specialties.size()));
                
                userRepository.save(professional);
                System.out.println("Profissional '" + professional.getFullName() + "' criado.");
            }
        }
    }
}