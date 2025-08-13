package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.repository.UserRepository;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de segurança que intercepta todas as requisições para validar o token JWT.
 */
@Component // Marca esta classe como um componente gerenciado pelo Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

 @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    
    var tokenJWT = recoverToken(request);

    if (tokenJWT != null) {
        var subject = jwtService.validateToken(tokenJWT);
        
        if (subject != null && !subject.isEmpty()) {
            // Busca o usuário no banco de dados
            UserDetails user = userRepository.findByLogin(subject).orElse(null);

            // VERIFICA SE O USUÁRIO EXISTE ANTES DE TENTAR AUTENTICAR
            if (user != null) {
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    // Continua o fluxo da requisição independentemente do resultado
    filterChain.doFilter(request, response);
}

    /**
     * Extrai o token JWT do cabeçalho "Authorization" da requisição.
     */
    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Remove o prefixo "Bearer " para obter apenas o token
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
