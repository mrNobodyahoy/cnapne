package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.ApiErrorDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthRequestDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.auth.AuthResponseDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Operation(
        summary = "Autentica um usuário e retorna um token JWT",
        description = "Este endpoint realiza a autenticação do usuário com base no login e senha.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Autenticação bem-sucedida. O token JWT e a role do usuário são retornados.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Dados inválidos. O corpo da requisição não atende às validações.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Acesso negado. Login, senha ou permissões inválidas.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDTO.class))
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO authData) {
        AuthResponseDTO response = authService.authenticate(authData);
        return ResponseEntity.ok(response);
    }
}