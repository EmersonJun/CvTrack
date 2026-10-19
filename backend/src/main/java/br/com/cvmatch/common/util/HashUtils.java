package br.com.cvmatch.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class HashUtils {

    private HashUtils() {
        // classe utilitária: não deve ser instanciada
    }

    /** SHA-256 em hexadecimal (64 caracteres). Mesmo texto = mesmo hash, sempre. */
    public static String sha256(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Toda JVM é obrigada a ter SHA-256; se faltar, é erro de ambiente
            throw new IllegalStateException("SHA-256 indisponível nesta JVM", e);
        }
    }
}
