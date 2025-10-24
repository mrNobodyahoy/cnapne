package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDateTime; // Importar
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.EmailDto;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.ResetPasswordRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception.ResourceNotFoundException;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import jakarta.transaction.Transactional;

/**
 * autenticação de usuários.
 */
@Service
public class AuthService {

    @Autowired
    private EmailPublisherService emailPublisherService;
    @Autowired
    private UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(authRequest.email(),
                    authRequest.password());

            var authentication = this.authenticationManager.authenticate(authenticationToken);

            var user = (User) authentication.getPrincipal();

            if (!user.isActive()) {
                throw new AuthenticationException("Este usuário está desativado no sistema.") {
                };
            }

            String token = jwtService.generateToken(user);
            Role role = user.getProfile().getName();

            return new AuthResponseDTO(
                    token,
                    user.getEmail(),
                    role);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Login ou senha inválidos.");
        } catch (AuthenticationException e) {
            throw e;
        }
    }

    public void processarPedidoRecuperacaoSenha(String emailUsuario) {
        Optional<User> userOptional = userRepository.findByEmail(emailUsuario);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();

        user.setRecoveryToken(token);
        user.setRecoveryTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String nomeUsuario = user.getName();
        String link = "http://localhost:5173/resetar-senha?token=" + token;

        Map<String, Object> props = new HashMap<>();
        props.put("nomeUsuario", nomeUsuario);
        props.put("linkRecuperacao", link);

        EmailDto email = new EmailDto(
                emailUsuario,
                "Recuperação de Senha - Sistema CNAPNE",
                "email/recuperacao-senha",
                props);

        emailPublisherService.scheduleEmail(email);
    }

    @Transactional
    public void resetarSenhaFinal(ResetPasswordRequestDTO dto) {
        User user = userRepository.findByRecoveryToken(dto.token())
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido ou expirado."));

        if (user.getRecoveryTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Token inválido ou expirado.");
        }

        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setRecoveryToken(null);
        user.setRecoveryTokenExpiry(null);

        userRepository.save(user);
    }
}