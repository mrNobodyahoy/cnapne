package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para ser lançada quando um recurso ou dado esperado não é
 * encontrado no sistema.
 * * A anotação @ResponseStatus(HttpStatus.NOT_FOUND) faz com que o Spring retorne
 * o código HTTP 404 (Not Found) automaticamente quando esta exceção é lançada.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
