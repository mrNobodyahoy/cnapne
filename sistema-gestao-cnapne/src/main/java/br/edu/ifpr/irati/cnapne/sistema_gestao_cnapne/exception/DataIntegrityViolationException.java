package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para ser lançada quando uma operação viola a integridade dos dados,
 * como a tentativa de inserir um registro com um valor único que já existe.
 * * A anotação @ResponseStatus(HttpStatus.CONFLICT) faz com que o Spring retorne
 * o código HTTP 409 (Conflict) automaticamente quando esta exceção é lançada.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DataIntegrityViolationException extends RuntimeException {

    public DataIntegrityViolationException(String message) {
        super(message);
    }

    public DataIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
