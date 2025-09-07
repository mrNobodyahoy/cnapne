package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.Student;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;

/**
 * autenticação de usuários.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
            if (user.getProfile().getName() == Role.ESTUDANTE) {
                Student student = (Student) user;
                Date dataNascimento = student.getBirthDate();

                if (dataNascimento != null) {
                    LocalDate nascimento = dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int idade = Period.between(nascimento, LocalDate.now()).getYears();
                    if (idade < 18) {
                        throw new AuthenticationException("Acesso restrito para menores de 18 anos."){};
                    }

                }
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
}
