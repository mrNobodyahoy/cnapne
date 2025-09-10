package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

// Usar 'record' é uma forma moderna e concisa de criar DTOs no Java.
// Se você estiver usando uma versão mais antiga do Java, pode usar uma classe normal.
public record ApiErrorDTO(
    String message,
    HttpStatus status,
    int statusCode,
    LocalDateTime timestamp
) {
    /**
     * Este é o construtor que o nosso Controller precisa.
     * Ele simplifica a criação do DTO, recebendo apenas o status e a mensagem.
     */
    public ApiErrorDTO(HttpStatus status, String message) {
        this(message, status, status.value(), LocalDateTime.now());
    }
}