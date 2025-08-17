package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Profile;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.ProfileRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String login, String rawPassword, Role role) {
        Profile profile = profileRepository.findByName(role)
                .orElseGet(() -> {
                    Profile newProfile = new Profile();
                    newProfile.setName(role);
                    return profileRepository.save(newProfile);
                });

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);
        user.setProfile(profile);

        return userRepository.save(user);
    }

}
