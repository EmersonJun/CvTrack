package br.com.cvmatch.curriculo.dto;

import java.time.Instant;
import java.util.UUID;

public record CurriculoDetalheResponse(
        UUID id,
        String titulo,
        int versao,
        String nomeArquivo,
        int totalCaracteres,
        Instant criadoEm,
        String textoExtraido
) {
}
