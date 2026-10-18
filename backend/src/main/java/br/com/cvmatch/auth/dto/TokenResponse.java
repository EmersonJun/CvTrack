package br.com.cvmatch.auth.dto;

/**
 * @param tipo            sempre "Bearer": indica como enviar o token no cabeçalho
 * @param expiraEmSegundos permite ao frontend saber quando pedir novo login
 */
public record TokenResponse(String token, String tipo, long expiraEmSegundos) {

    public static TokenResponse bearer(String token, long expiraEmSegundos) {
        return new TokenResponse(token, "Bearer", expiraEmSegundos);
    }
}
