package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List; // Importe ArrayList

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Professional;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Responsible; // Importe Responsible
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;     // Importe Student
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // --- CRIAÇÃO DO USUÁRIO COORDENADOR ---
        if (userRepository.findByEmail("coordenador@gmail.com").isEmpty()) {
            Profile coordProfile = profileRepository.findByName(Role.COORDENACAO_CNAPNE)
                .orElseThrow(() -> new RuntimeException("Perfil COORDENACAO_CNAPNE não foi encontrado."));

            Professional coord = new Professional();
            coord.setEmail("coordenador@gmail.com");
            coord.setPassword(passwordEncoder.encode("admin123"));
            coord.setProfile(coordProfile);
            coord.setActive(true);
            coord.setFullName("Coordenador Padrão");
            coord.setSpecialty("Coordenação CNAPNE");

            userRepository.save(coord);
            System.out.println("Usuário 'coordenador' padrão criado com sucesso.");
        }
        
        // ======================================================================
        //               >>> INÍCIO DA POPULAÇÃO DE ESTUDANTES <<<
        // ======================================================================

        // Busca o perfil de Estudante uma única vez
        Profile studentProfile = profileRepository.findByName(Role.ESTUDANTE)
            .orElseThrow(() -> new RuntimeException("Perfil ESTUDANTE não encontrado."));

        // --- ESTUDANTE 1: ANA SILVA (MAIOR DE IDADE) ---
        if (userRepository.findByEmail("ana.silva@email.com").isEmpty()) {
            Student ana = new Student();
            
            // Dados de User
            ana.setEmail("ana.silva@email.com");
            ana.setPassword(passwordEncoder.encode("aluno123"));
            ana.setActive(true);
            ana.setProfile(studentProfile);

            // Dados de Student
            ana.setCompleteName("Ana Silva");
            ana.setRegistration("2024001001");
            ana.setTeam("INFO3");
            ana.setBirthDate(Date.from(LocalDate.of(2005, 5, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            ana.setPhone("42999887766");
            ana.setGender("Feminino");
            ana.setEthnicity("Branca");
            ana.setStatus("ATIVO");

            userRepository.save(ana);
            System.out.println("Estudante 'Ana Silva' criada com sucesso.");
        }

        // --- ESTUDANTE 2: BRUNO COSTA (MENOR DE IDADE COM RESPONSÁVEL) ---
        if (userRepository.findByEmail("bruno.costa@email.com").isEmpty()) {
            Student bruno = new Student();

            // Dados de User
            bruno.setEmail("bruno.costa@email.com");
            bruno.setPassword(passwordEncoder.encode("aluno123"));
            bruno.setActive(true);
            bruno.setProfile(studentProfile);

            // Dados de Student
            bruno.setCompleteName("Bruno Costa");
            bruno.setRegistration("2024002002");
            bruno.setTeam("AGRO2");
            bruno.setBirthDate(Date.from(LocalDate.of(2008, 10, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            bruno.setPhone("42988776655");
            bruno.setGender("Masculino");
            bruno.setEthnicity("Parda");
            bruno.setStatus("ATIVO");

            // Criação do Responsável
            Responsible maria = new Responsible();
            maria.setCompleteName("Maria Costa");
            maria.setEmail("maria.costa@email.com");
            maria.setPhone("42988112233");
            maria.setKinship("Mãe");
            maria.setStudent(bruno); // << Associa o responsável ao estudante

            // Adiciona o responsável à lista do estudante
            bruno.setResponsibles(new ArrayList<>(List.of(maria)));

            userRepository.save(bruno); // << Salva o estudante (o responsável será salvo em cascata)
            System.out.println("Estudante 'Bruno Costa' e sua responsável foram criados com sucesso.");
        }

        // ======================================================================
        //                >>> FIM DA POPULAÇÃO DE ESTUDANTES <<<
        // ======================================================================
    }
}