package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;


@Service
public class JwtService {

    // Injeta a chave secreta do seu application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("CNAPNE API") 
                    .withSubject(user.getEmail()) 
                    .withExpiresAt(getExpirationDate()) 
                    .sign(algorithm); 
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

   
    public String validateToken(String tokenJWT) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("CNAPNE API")
                    .build()
                    .verify(tokenJWT) 
                    .getSubject(); 
        } catch (JWTVerificationException exception) {
            return "Token Invalido";
        }
    }

  
    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
