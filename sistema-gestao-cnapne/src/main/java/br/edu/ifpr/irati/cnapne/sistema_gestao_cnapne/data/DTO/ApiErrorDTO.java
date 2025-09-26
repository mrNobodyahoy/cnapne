package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public record ApiErrorDTO(
        String message,
        HttpStatus status,
        int statusCode,
        LocalDateTime timestamp) {

    public ApiErrorDTO(HttpStatus status, String message) {
        this(message, status, status.value(), LocalDateTime.now());
    }
}