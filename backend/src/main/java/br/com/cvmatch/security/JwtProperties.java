package br.com.cvmatch.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Liga as propriedades "cvmatch.jwt.*" do application.yml a um objeto tipado,
 * em vez de espalhar @Value pelo código.
 */
@ConfigurationProperties(prefix = "cvmatch.jwt")
public record JwtProperties(String secret, long expiracaoMinutos) {
}
