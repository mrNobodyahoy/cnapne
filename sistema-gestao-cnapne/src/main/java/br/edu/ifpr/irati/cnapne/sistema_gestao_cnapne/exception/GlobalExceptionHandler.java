package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Este método irá "pegar" qualquer AuthenticationException lançada na sua aplicação
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        // Criamos um corpo de resposta JSON com a mensagem da exceção
        Map<String, String> errorResponse = Map.of("message", ex.getMessage());
        
        // Retornamos o JSON com o status 403 Forbidden
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}