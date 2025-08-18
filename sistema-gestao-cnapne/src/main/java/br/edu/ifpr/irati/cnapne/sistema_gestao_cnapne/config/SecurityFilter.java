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

/*
 validar o token JWT.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {

        var tokenJWT = recoverToken(request);

        if (tokenJWT != null) {
            var subject = jwtService.validateToken(tokenJWT);

            if (subject != null && !subject.isEmpty()) {
                UserDetails user = userRepository.findByEmail(subject).orElse(null);

                if (user != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /*
     Extrai o token JWT do cabeçalho.
     */
    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // apenas o token
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
