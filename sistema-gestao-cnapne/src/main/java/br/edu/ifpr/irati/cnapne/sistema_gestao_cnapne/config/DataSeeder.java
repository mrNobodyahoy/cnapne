    package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

    import java.util.Arrays;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Component;

    import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
    import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
    import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
    import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
    import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;

    /**
     * Popular o banco de dados
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
            Arrays.stream(Role.values()).forEach(roleEnum -> {
                if (profileRepository.findByName(roleEnum).isEmpty()) {
                    Profile profile = new Profile();
                    profile.setName(roleEnum);
                    profileRepository.save(profile);
                    System.out.println("Perfil criado: " + roleEnum.name());
                }
            });

            if (userRepository.findByEmail("coordenador@gmail.com").isEmpty()) {
                Profile coordProfile = profileRepository.findByName(Role.COORDENACAO_CNAPNE)
                    .orElseThrow(() -> new RuntimeException("Perfil COORDENACAO_CNAPNE não foi encontrado no banco."));

                User adminUser = new User();
                adminUser.setEmail("coordenador@gmail.com");
                adminUser.setPassword(passwordEncoder.encode("admin123")); 
                adminUser.setProfile(coordProfile);
                adminUser.setActive(true);
                userRepository.save(adminUser);
                System.out.println("Usuário 'coordenador' padrão criado com sucesso.");
            }
        }
    }