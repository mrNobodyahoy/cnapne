package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Esta classe é executada na inicialização da aplicação para popular o banco de dados
 * com dados essenciais, como os perfis de usuário e um administrador padrão.
 */
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
        // 1. Criar os Perfis (Roles) se eles não existirem
        Arrays.stream(Role.values()).forEach(roleEnum -> {
            if (profileRepository.findByName(roleEnum).isEmpty()) {
                Profile profile = new Profile();
                profile.setName(roleEnum);
                profileRepository.save(profile);
                System.out.println("Perfil criado: " + roleEnum.name());
            }
        });

        // 2. Criar um usuário Coordenador padrão se ele não existir
        if (userRepository.findByLogin("coordenador").isEmpty()) {
            // Busca o perfil de coordenação que garantimos que existe no passo anterior
            Profile coordProfile = profileRepository.findByName(Role.COORDENACAO_CNAPNE)
                .orElseThrow(() -> new RuntimeException("Perfil COORDENACAO_CNAPNE não foi encontrado no banco."));

            User adminUser = new User();
            adminUser.setLogin("coordenador");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Senha padrão para desenvolvimento
            adminUser.setProfile(coordProfile);
            adminUser.setActive(true);
            userRepository.save(adminUser);
            System.out.println("Usuário 'coordenador' padrão criado com sucesso.");
        }
    }
}