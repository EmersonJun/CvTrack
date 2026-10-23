package br.com.cvmatch.auth.dto;

public record TokenResponse(String token, String tipo, long expiraEmSegundos) {

    public static TokenResponse bearer(String token, long expiraEmSegundos) {
        return new TokenResponse(token, "Bearer", expiraEmSegundos);
    }
}
