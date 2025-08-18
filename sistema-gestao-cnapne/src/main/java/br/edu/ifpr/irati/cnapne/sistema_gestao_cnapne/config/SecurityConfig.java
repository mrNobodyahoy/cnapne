package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Login
                        .requestMatchers("/api/v1/auth/login").permitAll()

                        // Estudantes
                        .requestMatchers(HttpMethod.POST, "/api/v1/students").hasAuthority("ROLE_COORDENACAO_CNAPNE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/**").permitAll()

                        // Profissionais
                        .requestMatchers(HttpMethod.POST, "/api/v1/professionals")
                        .hasAuthority("ROLE_COORDENACAO_CNAPNE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/professionals/**")
                        .hasAnyAuthority("ROLE_COORDENACAO_CNAPNE", "ROLE_EQUIPE_MULTIDISCIPLINAR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/professionals/**")
                        .hasAuthority("ROLE_COORDENACAO_CNAPNE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/professionals/**")
                        .hasAuthority("ROLE_COORDENACAO_CNAPNE")

                        // Documentos
                        .requestMatchers(HttpMethod.POST, "/api/v1/students/*/documents")
                        .hasAnyAuthority("ROLE_COORDENACAO_CNAPNE", "ROLE_EQUIPE_MULTIDISCIPLINAR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/*/documents/**")
                        .hasAnyAuthority("ROLE_COORDENACAO_CNAPNE", "ROLE_EQUIPE_MULTIDISCIPLINAR", "ROLE_ESTUDANTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/students/*/documents/**")
                        .hasAuthority("ROLE_COORDENACAO_CNAPNE")

                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
