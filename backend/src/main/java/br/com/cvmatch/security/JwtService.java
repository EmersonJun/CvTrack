package br.com.cvmatch.security;

import br.com.cvmatch.usuario.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Gera e valida tokens JWT assinados com HMAC-SHA (chave simétrica).
 *
 * O token carrega id, e-mail e papel do usuário. Assim, cada requisição é
 * autenticada SEM consultar o banco (API stateless).
 * Contrapartida: mudanças de papel só valem quando o token expira.
 */
@Slf4j
@Service
public class JwtService {

    /** HS256 exige chave de no mínimo 256 bits = 32 bytes. */
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(JwtProperties propriedades) {
        byte[] segredo = propriedades.secret() == null
                ? new byte[0]
                : propriedades.secret().getBytes(StandardCharsets.UTF_8);
        // Fail fast: melhor a aplicação não subir do que rodar com chave fraca
        if (segredo.length < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException(
                    "JWT_SECRET deve ter pelo menos " + TAMANHO_MINIMO_SEGREDO + " caracteres");
        }
        this.chave = Keys.hmacShaKeyFor(segredo);
        this.expiracaoMinutos = propriedades.expiracaoMinutos();
    }

    public String gerarToken(UsuarioAutenticado usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuario.id().toString())
                .claim("email", usuario.email())
                .claim("role", usuario.role().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }

    /**
     * Valida assinatura e expiração. Devolve vazio (em vez de lançar exceção)
     * para o filtro simplesmente seguir sem autenticar; quem barra a
     * requisição depois é o Spring Security, com 401.
     */
    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(new UsuarioAutenticado(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class),
                    null,
                    Role.valueOf(claims.get("role", String.class))));
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT rejeitado: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public long getExpiracaoSegundos() {
        return expiracaoMinutos * 60;
    }
}
