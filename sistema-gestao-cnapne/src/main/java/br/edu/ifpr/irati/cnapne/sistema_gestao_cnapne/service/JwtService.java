package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * gerar e validar tokens JWT.
 */
@Service
public class JwtService {

    // Injeta a chave secreta do seu application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("CNAPNE API") // Emissor do token
                    .withSubject(user.getLogin()) // Identificador do usuário (o login)
                    .withExpiresAt(getExpirationDate()) // Define a data de expiração
                    .sign(algorithm); // Assina o token
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida um token JWT e retorna o "subject" (login do usuário) se for válido.
     * @param tokenJWT O token a ser validado.
     * @return O login do usuário se o token for válido; caso contrário, uma String vazia.
     */
    public String validateToken(String tokenJWT) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("CNAPNE API")
                    .build()
                    .verify(tokenJWT) // Valida o token
                    .getSubject(); // Extrai o subject (login)
        } catch (JWTVerificationException exception) {
            // Se o token for inválido (expirado, assinatura errada, etc.), retorna vazio
            return "";
        }
    }

    /**
     * Calcula o momento exato em que o token deve expirar.
     * @return Um Instant representando a data/hora de expiração.
     */
    private Instant getExpirationDate() {
        // Define a expiração para 2 horas a partir de agora, no fuso horário de Brasília
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
