package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import java.sql.Time;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.FollowUp;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Responsible;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.FollowUpRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfessionalRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ServiceRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.StudentRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final FollowUpRepository followUpRepository;

    public DataSeeder(ProfileRepository profileRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder, StudentRepository studentRepository,
            ProfessionalRepository professionalRepository, ServiceRepository serviceRepository,
            FollowUpRepository followUpRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
        this.followUpRepository = followUpRepository;
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
        }

        // --- POPULAÇÃO DE ESTUDANTES (30) ---
        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
                .orElseThrow(() -> new RuntimeException("Perfil ESTUDANTE não encontrado."));

        if (studentRepository.count() < 30) {
            System.out.println("--- Criando Estudantes ---");
            for (int i = 1; i <= 30; i++) {
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
                }
            }
        }

        // --- POPULAÇÃO DE PROFISSIONAIS (30) ---
        if (professionalRepository.count() < 30) {
            System.out.println("--- Criando Profissionais ---");
            List<Role> professionalRoles = List.of(Role.EQUIPE_ACOMPANHAMENTO, Role.EQUIPE_AEE);
            List<String> specialties = List.of("Psicologia", "Pedagogia", "Assistência Social", "Intérprete de Libras");
            List<String> todosNomes = new java.util.ArrayList<>();
            todosNomes.addAll(nomesFemininos);
            todosNomes.addAll(nomesMasculinos);

            for (int i = 1; i <= 30; i++) {
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
                }
            }
        }

        // --- BUSCANDO DADOS EXISTENTES PARA AS ASSOCIAÇÕES ---
        List<Student> students = studentRepository.findAll();
        List<Professional> professionals = professionalRepository.findAll();

        if (students.isEmpty() || professionals.isEmpty()) {
            System.out.println("Aguardando a criação de estudantes e profissionais para popular as sessões...");
            return;
        }

        // --- POPULAÇÃO DE ATENDIMENTOS (20) ---
        if (serviceRepository.count() == 0) {
            System.out.println("--- Criando Atendimentos ---");
            List<String> serviceTypes = List.of("Apoio Pedagógico", "Atendimento Psicológico", "Orientação Vocacional");
            List<String> locations = List.of("Sala CNAPNE 1", "Online", "Biblioteca");
            List<String> statuses = List.of("REALIZADO", "AGENDADO", "CANCELADO");

            for (int i = 0; i < 20; i++) {
                Service atendimento = new Service();
                Student student = students.get(i % students.size());
                Professional professional = professionals.get(i % professionals.size());

                String status = statuses.get(i % statuses.size());

                atendimento.setStudent(student);
                atendimento.setProfessionals(Collections.singletonList(professional));
                atendimento.setSessionDate(LocalDate.now().minusDays(i * 3));
                atendimento.setSessionTime(Time.valueOf(10 + (i % 8) + ":00:00"));
                atendimento.setSessionLocation(locations.get(i % locations.size()));
                atendimento.setStatus(status);
                atendimento.setTypeService(serviceTypes.get(i % serviceTypes.size()));
                atendimento.setDescriptionService("Descrição do atendimento para o aluno " + student.getCompleteName());
                atendimento.setTasks("Tarefas definidas na sessão de " + atendimento.getTypeService() + ".");

                // ADICIONADO: Preenchimento dos novos campos
                atendimento.setObjectives(
                        "Objetivo do " + atendimento.getTypeService() + " para " + student.getCompleteName());
                if ("REALIZADO".equals(status)) {
                    atendimento.setResults("Resultados positivos foram observados durante a sessão.");
                }

                serviceRepository.save(atendimento);
            }
        }

        // --- POPULAÇÃO DE ACOMPANHAMENTOS (20) ---
        if (followUpRepository.count() == 0) {
            System.out.println("--- Criando Acompanhamentos ---");
            List<String> locations = List.of("Sala de Reuniões", "Online", "Pátio");
            List<String> statuses = List.of("REALIZADO", "AGENDADO"); // Ajustado para corresponder ao front-end
            List<String> areas = List.of("Pedagógica", "Psicossocial", "Familiar", "Adaptação Curricular");

            for (int i = 0; i < 20; i++) {
                FollowUp acompanhamento = new FollowUp();
                Student student = students.get(i % students.size());
                Professional professional1 = professionals.get(i % professionals.size());
                Professional professional2 = professionals.get((i + 1) % professionals.size());

                String status = statuses.get(i % statuses.size());

                acompanhamento.setStudent(student);
                acompanhamento.setProfessionals(Arrays.asList(professional1, professional2));
                acompanhamento.setSessionDate(LocalDate.now().minusDays(i * 5));
                acompanhamento.setSessionTime(Time.valueOf(13 + (i % 4) + ":30:00"));
                acompanhamento.setSessionLocation(locations.get(i % locations.size()));
                acompanhamento.setStatus(status);
                acompanhamento
                        .setDescription("Descrição do acompanhamento geral do aluno " + student.getCompleteName());
                acompanhamento.setTasks("Verificar progresso das atividades anteriores.");

                // ADICIONADO: Preenchimento dos novos campos
                acompanhamento.setAreasCovered(areas.get(i % areas.size()));
                if ("REALIZADO".equals(status)) {
                    acompanhamento.setNextSteps("Definir novas estratégias para o próximo bimestre.");
                }

                followUpRepository.save(acompanhamento);
            }
        }
    }
}