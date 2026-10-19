package br.com.cvmatch.curriculo.dto;

import java.time.Instant;
import java.util.UUID;

/** Resumo usado em listagens: não inclui o texto extraído (pode ser grande). */
public record CurriculoResponse(
        UUID id,
        String titulo,
        int versao,
        String nomeArquivo,
        int totalCaracteres,
        Instant criadoEm
) {
}
