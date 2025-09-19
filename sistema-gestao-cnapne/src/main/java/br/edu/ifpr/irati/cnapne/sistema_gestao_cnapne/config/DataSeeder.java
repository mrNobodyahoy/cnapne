package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    public DataSeeder(ProfileRepository profileRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- Listas de Nomes para Geração Aleatória ---
        List<String> nomesMasculinos = Arrays.asList("Miguel", "Arthur", "Heitor", "Bernardo", "Davi", "Lucas", "Pedro",
                "Gabriel", "Matheus", "Enzo");
        List<String> nomesFemininos = Arrays.asList("Helena", "Alice", "Laura", "Maria Alice", "Sophia", "Manuela",
                "Maitê", "Liz", "Cecília", "Isabella");
        List<String> sobrenomes = Arrays.asList("Silva", "Santos", "Oliveira", "Souza", "Rodrigues", "Ferreira",
                "Alves", "Pereira", "Lima", "Gomes", "Costa", "Ribeiro", "Martins", "Carvalho");

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
            coord.setFullName("Coordenador Padrão CNAPNE");
            coord.setSpecialty("Coordenação Geral");

            userRepository.save(coord);
            System.out.println("Usuário 'coordenador' padrão criado com sucesso.");
        }

        // --- POPULAÇÃO DE ESTUDANTES (20) ---
        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
                .orElseThrow(() -> new RuntimeException("Perfil ESTUDANTE não encontrado."));

        for (int i = 1; i <= 20; i++) {
            String email = "aluno" + i + "@ifpr.edu.br";
            if (userRepository.findByEmail(email).isEmpty()) {

                String primeiroNome;
                String genero;
                if (i % 2 == 0) {
                    primeiroNome = nomesMasculinos.get(ThreadLocalRandom.current().nextInt(nomesMasculinos.size()));
                    genero = "homem_cis";
                } else {
                    primeiroNome = nomesFemininos.get(ThreadLocalRandom.current().nextInt(nomesFemininos.size()));
                    genero = "mulher_cis";
                }
                String sobrenome1 = sobrenomes.get(ThreadLocalRandom.current().nextInt(sobrenomes.size()));
                String sobrenome2 = sobrenomes.get(ThreadLocalRandom.current().nextInt(sobrenomes.size()));
                String nomeCompleto = primeiroNome + " " + sobrenome1 + " " + sobrenome2;

                Student student = new Student();
                student.setEmail(email);
                student.setPassword(passwordEncoder.encode("aluno123"));
                student.setActive(i % 10 != 0);
                student.setProfile(studentProfile);
                student.setCompleteName(nomeCompleto);
                student.setRegistration("2024" + String.format("%04d", i));
                student.setTeam("TURMA " + ((i % 4) + 1));

                // <<< CORREÇÃO APLICADA AQUI >>>
                LocalDate dataNascimentoLocal = LocalDate.of(2005 + (i % 5), (i % 12) + 1, (i % 28) + 1);
                student.setBirthDate(dataNascimentoLocal);

                student.setPhone("4299" + (100000 + i));
                student.setGender(genero);
                student.setEthnicity(i % 3 == 0 ? "branca" : "parda");
                student.setStatus(student.isActive() ? "ATIVO" : "INATIVO");

                int age = Period.between(dataNascimentoLocal, LocalDate.now()).getYears();
                if (age < 18) {
                    Responsible resp = new Responsible();
                    String nomeResponsavel = (genero.equals("homem_cis") ? "Pai do " : "Mãe da ") + primeiroNome;
                    resp.setCompleteName(nomeResponsavel + " " + sobrenome1);
                    resp.setEmail("responsavel" + i + "@gmail.com");
                    resp.setPhone("4298" + (200000 + i));
                    resp.setKinship(genero.equals("homem_cis") ? "Pai" : "Mãe");
                    resp.setStudent(student);
                    student.setResponsibles(List.of(resp));
                }

                userRepository.save(student);
                System.out.println("Estudante '" + student.getCompleteName() + "' criado.");
            }
        }

        // --- POPULAÇÃO DE PROFISSIONAIS (20) ---
        List<Role> professionalRoles = List.of(Role.EQUIPE_ACOMPANHAMENTO, Role.EQUIPE_AEE);
        List<String> specialties = List.of("Psicologia", "Pedagogia", "Assistência Social", "Intérprete de Libras");
        List<String> todosNomes = new java.util.ArrayList<>();
        todosNomes.addAll(nomesFemininos);
        todosNomes.addAll(nomesMasculinos);

        for (int i = 1; i <= 20; i++) {
            String email = "profissional" + i + "@ifpr.edu.br";
            if (userRepository.findByEmail(email).isEmpty()) {
                Role role = professionalRoles.get(i % professionalRoles.size());
                Profile profProfile = profileRepository.findByName(role)
                        .orElseThrow(() -> new RuntimeException("Perfil " + role + " não encontrado."));

                String primeiroNome = todosNomes.get(ThreadLocalRandom.current().nextInt(todosNomes.size()));
                String sobrenome1 = sobrenomes.get(ThreadLocalRandom.current().nextInt(sobrenomes.size()));
                String nomeCompleto = primeiroNome + " " + sobrenome1;

                Professional professional = new Professional();
                professional.setEmail(email);
                professional.setPassword(passwordEncoder.encode("profissional123"));
                professional.setActive(i % 10 != 0);
                professional.setProfile(profProfile);
                professional.setFullName(nomeCompleto);
                professional.setSpecialty(specialties.get(i % specialties.size()));

                userRepository.save(professional);
                System.out.println("Profissional '" + professional.getFullName() + "' (" + professional.getSpecialty()
                        + ") criado.");
            }
        }
    }
}