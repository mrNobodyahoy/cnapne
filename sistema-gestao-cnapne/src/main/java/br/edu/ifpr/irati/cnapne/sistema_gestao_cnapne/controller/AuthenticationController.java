package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import java.util.Map;

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
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.ResetPasswordRequestDTO;
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
                                .secure(false)
                                .sameSite("Strict")
                                .path("/")
                                .maxAge(2 * 60 * 60)
                                .build();

                httpServletResponse.addHeader("Set-Cookie", jwtCookie.toString());

                return ResponseEntity.ok(new LoginResponseDTO(
                                authInternalResponse.email(),
                                authInternalResponse.role()));
        }

        @GetMapping("/me")
        public ResponseEntity<LoginResponseDTO> me(Authentication authentication) {
                User userDetails = (User) authentication.getPrincipal();

                var response = new LoginResponseDTO(
                                userDetails.getEmail(),
                                userDetails.getProfile().getName());

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Encerrar a sessão do usuário", description = "Invalida o cookie de autenticação JWT, encerrando a sessão do usuário.", responses = {
                        @ApiResponse(responseCode = "204", description = "Logout bem-sucedido", content = @Content)
        })
        @PostMapping("/logout")
        public ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
                ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                                .httpOnly(true)
                                .secure(false)
                                .sameSite("Strict")
                                .path("/")
                                .maxAge(0)
                                .build();

                httpServletResponse.addHeader("Set-Cookie", jwtCookie.toString());

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/recuperacao-senha")
        public ResponseEntity<String> solicitarRecuperacao(@RequestBody Map<String, String> payload) {
                String email = payload.get("email");
                if (email == null || email.isEmpty()) {
                        return ResponseEntity.badRequest().body("O e-mail é obrigatório.");
                }

                authService.processarPedidoRecuperacaoSenha(email);

                return ResponseEntity.ok("Se um usuário com este e-mail existir, um link de recuperação foi enviado.");
        }

        @PostMapping("/resetar-senha-final")
        public ResponseEntity<String> resetarSenhaFinal(@RequestBody @Valid ResetPasswordRequestDTO payload) {
                authService.resetarSenhaFinal(payload);
                return ResponseEntity.ok("Senha redefinida com sucesso.");
        }
}