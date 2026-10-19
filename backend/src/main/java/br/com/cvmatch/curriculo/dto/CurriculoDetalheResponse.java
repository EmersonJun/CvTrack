package br.com.cvmatch.curriculo.dto;

import java.time.Instant;
import java.util.UUID;

/** Detalhe de uma versão: inclui o texto extraído, para o usuário conferir o que a IA vai ler. */
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
