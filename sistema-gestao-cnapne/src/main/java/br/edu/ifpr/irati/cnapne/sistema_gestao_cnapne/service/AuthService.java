package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.enums.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por orquestrar a lógica de autenticação de usuários.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService; // <-- CORREÇÃO: Usar JwtService

    // Injeção de dependências via construtor
    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) { // <-- CORREÇÃO: Usar JwtService
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService; // <-- CORREÇÃO: Usar JwtService
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        try {
            // 1. Cria o objeto de autenticação para o Spring Security
            var authenticationToken = new UsernamePasswordAuthenticationToken(authRequest.login(), authRequest.password());

            // 2. Efetivamente autentica (valida login e senha)
            var authentication = this.authenticationManager.authenticate(authenticationToken);

            // 3. Se a autenticação foi bem-sucedida, pega os dados do usuário
            var user = (User) authentication.getPrincipal();

            // 4. VALIDAÇÃO DE NEGÓCIO: Verifica se o usuário está ativo
            if (!user.isActive()) {
                throw new AuthenticationException("Este usuário está desativado no sistema.") {};
            }

            // 5. Gera o token JWT
            String token = jwtService.generateToken(user); // <-- CORREÇÃO: Usar jwtService
            Role role = user.getProfile().getName();

            // 6. Retorna a resposta com o token e a role
            return new AuthResponseDTO(token, role);

        } catch (BadCredentialsException e) {
            // Captura o erro específico de credenciais erradas para uma mensagem clara
            throw new BadCredentialsException("Login ou senha inválidos.");
        } catch (AuthenticationException e) {
            // Captura outras exceções de autenticação (como a de usuário inativo)
            throw e;
        }
    }
}
