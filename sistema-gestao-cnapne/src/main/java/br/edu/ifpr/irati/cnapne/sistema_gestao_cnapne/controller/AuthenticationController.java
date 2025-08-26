package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.LoginResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoint para autenticação de usuários no sistema.")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthRequestDTO authData,
                                                  HttpServletResponse httpServletResponse) {

        AuthResponseDTO authInternalResponse = authService.authenticate(authData);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", authInternalResponse.token())
                .httpOnly(true)
                .secure(false) // 🔐 mudar pra true em produção (https)
                .sameSite("Strict")
                .path("/")
                .maxAge(2 * 60 * 60) // 2h
                .build();

        httpServletResponse.addHeader("Set-Cookie", jwtCookie.toString());

        return ResponseEntity.ok(new LoginResponseDTO(
                authInternalResponse.email(),
                authInternalResponse.role()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();

        var response = new LoginResponseDTO(
                userDetails.getEmail(),
                userDetails.getProfile().getName()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Encerrar a sessão do usuário",
        description = "Invalida o cookie de autenticação JWT, encerrando a sessão do usuário.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Logout bem-sucedido", 
                         content = @Content)
        }
    )
    @PostMapping("/logout") // 💡 Recomendado usar POST para ações que alteram o estado.
    public ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        // 1. Cria um cookie com o mesmo nome, mas com valor vazio.
        // 2. Define o maxAge como 0 para forçar o navegador a deletá-lo imediatamente.
        // 3. O path e o httpOnly devem ser iguais ao cookie de login para que a deleção funcione corretamente.
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // 🔐 Mantenha o mesmo valor do cookie de login
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // 💡 0 segundos para expiração imediata.
                .build();

        httpServletResponse.addHeader("Set-Cookie", jwtCookie.toString());

        return ResponseEntity.noContent().build(); // Retorna 204 No Content para indicar sucesso.
    }
}