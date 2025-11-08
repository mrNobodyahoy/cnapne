package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Autenticação
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/logout").permitAll()
                        .requestMatchers("/api/v1/auth/me").authenticated()
                        .requestMatchers("/api/v1/auth/recuperacao-senha").permitAll()
                        .requestMatchers("/api/v1/auth/resetar-senha-final").permitAll()
                        .requestMatchers("/api/v1/students/me").hasRole("ESTUDANTE")

                        // Documentos
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/*/documents/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/students/*/documents/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/documents/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/students/*/documents/**").permitAll()

                        // Estudantes
                        .requestMatchers(HttpMethod.POST, "/api/v1/students")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/students/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "ESTUDANTE", "EQUIPE_ACOMPANHAMENTO")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/students/**").hasRole("COORDENACAO_CNAPNE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/students/**").hasRole("COORDENACAO_CNAPNE")

                        // Profissionais
                        .requestMatchers("/api/v1/professionals/me")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/professionals/me")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/professionals/me/change-password")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        .requestMatchers(HttpMethod.GET, "/api/v1/professionals/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        .requestMatchers(HttpMethod.POST, "/api/v1/professionals")
                        .hasRole("COORDENACAO_CNAPNE")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/professionals/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/professionals/**")
                        .hasRole("COORDENACAO_CNAPNE")

                        // ATENDIMENTOS
                        .requestMatchers("/api/v1/atendimentos/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        // ACOMPANHMENTOS
                        .requestMatchers("/api/v1/acompanhamentos/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        // 🎓 Orientações do Professor
                        .requestMatchers("/api/v1/orientacoes-professor/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO")

                        // DASHBOArD
                        .requestMatchers("/api/v1/dashboard/**")
                        .hasAnyRole("COORDENACAO_CNAPNE", "EQUIPE_AEE", "EQUIPE_ACOMPANHAMENTO", "ESTUDANTE")
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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