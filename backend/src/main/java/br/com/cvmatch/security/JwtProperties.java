package br.com.cvmatch.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cvmatch.jwt")
public record JwtProperties(String secret, long expiracaoMinutos) {
}
