package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO Padrão para representar respostas de erro da API.
 * Isso garante que todas as mensagens de erro tenham uma estrutura consistente.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Não inclui campos nulos no JSON de resposta
@Schema(description = "Estrutura padrão para respostas de erro da API")
public record ApiErrorDTO(
    @Schema(description = "Carimbo de data e hora de quando o erro ocorreu", example = "2025-08-11T20:30:00")
    LocalDateTime timestamp,

    @Schema(description = "Código de status HTTP", example = "404")
    int status,

    @Schema(description = "Descrição do status HTTP", example = "Not Found")
    String error,

    @Schema(description = "Mensagem principal e legível do erro", example = "Estudante não encontrado com o ID: a4e8-b2f0...")
    String message,

    @Schema(description = "Caminho da URL que foi chamada", example = "/api/v1/students/a4e8-b2f0...")
    String path,

    @Schema(description = "Mapa contendo erros de validação específicos por campo. Presente apenas em erros de validação (status 400).")
    Map<String, String> validationErrors
) {
    // Construtor para erros gerais (sem erros de validação de campo)
    public ApiErrorDTO(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    // Construtor para erros de validação (com múltiplos erros de campo)
    public ApiErrorDTO(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(LocalDateTime.now(), status, error, message, path, validationErrors);
    }
}