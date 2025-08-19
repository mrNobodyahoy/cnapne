package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoint para autenticação de usuários no sistema.")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO authData,
            HttpServletResponse httpServletResponse) {
        // 1. Autentica o usuário e gera o DTO de resposta contendo o token e a role
        AuthResponseDTO authResponse = authService.authenticate(authData);

        // 2. Cria o cookie seguro com o token JWT
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", authResponse.token()) // Nome do cookie e valor (o token)
                .httpOnly(true) // Impede que o JavaScript no frontend acesse o cookie
                .secure(true) // Requer HTTPS (em produção, pode ser 'false' para testes em http localhost)
                .sameSite("Strict") // Protege contra ataques CSRF
                .path("/") // O cookie será válido para todas as rotas da sua API
                .maxAge(2 * 60 * 60) // Define a expiração do cookie (2 horas)
                .build();

        // 3. Adiciona o cookie ao cabeçalho da resposta HTTP
        httpServletResponse.addHeader("Set-Cookie", jwtCookie.toString());

        // 4. Retorna uma resposta JSON para o frontend.
        // O token é enviado como nulo no corpo, pois ele já foi enviado no cookie
        // seguro.
        // O frontend usará a 'role' para controlar a interface.
        return ResponseEntity.ok(new AuthResponseDTO(null, authResponse.role()));
    }
}